package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "marketitem_id"}))
public class RerolledItem {
    @Setter @Id @GeneratedValue Long id;
    @Getter @Setter @ManyToOne Player player;
    @Getter @Setter @ManyToOne MarketItem marketItem;
    @Getter @Setter LocalDateTime rerolledAt;

    //Used for limiting the amount or reroll per round
    @Getter @Setter Long roundId;


}
