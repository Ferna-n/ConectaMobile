package com.example.conectamobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText messageEditText;
    private Button sendButton;
    private TextView chatWithTextView;

    private FirebaseAuth auth;
    private DatabaseReference messagesRef;
    private String currentUserId;

    private Mqtt5AsyncClient mqttClient;
    private static final String MQTT_BROKER_URL = "broker.hivemq.com";
    private static final String MQTT_TOPIC = "conectamobile/chat";

    private Set<String> processedMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        currentUserId = auth.getCurrentUser().getUid();

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        chatWithTextView = findViewById(R.id.chatWithTextView);

        processedMessages = new HashSet<>();

        loadChatWithUser("receiverUserId");

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messageAdapter);

        initializeMQTT();

        loadMessagesFromFirebase();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void loadChatWithUser(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String chatWithName = snapshot.child("name").getValue(String.class);
                setChatWith(chatWithName);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error al cargar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setChatWith(String chatWithName) {
        chatWithTextView.setText("  " + (chatWithName != null ? chatWithName : ""));
    }

    private void initializeMQTT() {
        mqttClient = Mqtt5Client.builder()
                .identifier("ConectaMobile_" + System.currentTimeMillis())
                .serverHost(MQTT_BROKER_URL)
                .buildAsync();

        mqttClient.connectWith()
                .cleanStart(true)
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        runOnUiThread(() -> Toast.makeText(this, "Error al conectar MQTT", Toast.LENGTH_SHORT).show());
                    } else {
                        mqttClient.subscribeWith()
                                .topicFilter(MQTT_TOPIC)
                                .callback(publish -> {
                                    String messageText = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                                    processMQTTMessage(messageText);
                                })
                                .send();
                    }
                });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String senderName = snapshot.child("name").getValue(String.class);
                if (senderName == null || senderName.isEmpty()) {
                    senderName = "Desconocido";
                }

                Message message = new Message(currentUserId, senderName, messageText, System.currentTimeMillis());
                messagesRef.push().setValue(message);

                String formattedMessage = currentUserId + ":" + messageText;
                mqttClient.publishWith()
                        .topic(MQTT_TOPIC)
                        .payload(formattedMessage.getBytes(StandardCharsets.UTF_8))
                        .send();

                messageEditText.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessagesFromFirebase() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();
                    if (processedMessages.contains(key)) {
                        continue;
                    }

                    Message message = child.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                        processedMessages.add(key);
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processMQTTMessage(String messageText) {
        String[] parts = messageText.split(":", 2);
        if (parts.length < 2) {
            return;
        }

        String senderId = parts[0];
        String text = parts[1];

        if (senderId.equals(currentUserId)) {
            return;
        }

        for (Message existingMessage : messageList) {
            if (existingMessage.getSenderId().equals(senderId) &&
                    existingMessage.getText().equals(text)) {
                return;
            }
        }

        Message mqttMessage = new Message(senderId, "Otro Usuario", text, System.currentTimeMillis());
        processedMessages.add(text);
        messageList.add(mqttMessage);
        runOnUiThread(() -> {
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            messagesRecyclerView.scrollToPosition(messageList.size() - 1);
        });
    }
}
