package com.javamentor.qa.platform.models.entity.chat;

import com.javamentor.qa.platform.models.entity.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(generator = "Message_seq")
    private Long id;

    @Column
    @Type(type = "org.hibernate.type.TextType")
    private String message;

    @Column(name = "last_redaction_date", nullable = false)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @UpdateTimestamp
    private LocalDateTime lastRedactionDate;

    @Column(name = "persist_date", updatable = false)
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @CreationTimestamp
    private LocalDateTime persistDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User userSender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id")
    private Chat chat;


    public Message(String message, User userSender, Chat chat) {
        this.message = message;
        this.userSender = userSender;
        this.chat = chat;
    }

    public Message(String message) {
        this.message = message;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(id, message1.id) &&
                Objects.equals(message, message1.message) &&
                Objects.equals(lastRedactionDate, message1.lastRedactionDate) &&
                Objects.equals(persistDate, message1.persistDate) &&
                Objects.equals(userSender, message1.userSender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, lastRedactionDate, persistDate, userSender);
    }
}
