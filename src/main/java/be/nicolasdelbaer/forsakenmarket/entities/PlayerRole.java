package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlayerRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Column(nullable = false, unique = true)
    @Getter @Setter
    private String name;

    public PlayerRole(String roleName) {
        this.name = roleName;
    }
}