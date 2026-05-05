package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class CollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter private Long id;

    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    @Getter @Setter private ItemBlueprint itemBlueprint;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @Getter @Setter private Player player;

    @Column(nullable = false)
    @Getter @Setter private Long foundRoundId;

    @Column(nullable = false)
    @Getter @Setter private LocalDateTime foundAt;

}
