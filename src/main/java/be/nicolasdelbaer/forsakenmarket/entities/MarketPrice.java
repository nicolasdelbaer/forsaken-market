package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter @Setter
    private Integer currentPrice;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    private ItemBlueprint itemBlueprint;

    @Getter @Setter
    private Long roundId;

    @Getter @CreationTimestamp
    private LocalDateTime createdAt;

    public MarketPrice(Integer currentPrice, ItemBlueprint itemBlueprint, Long roundId) {
        this.currentPrice = currentPrice;
        this.itemBlueprint = itemBlueprint;
        this.roundId = roundId;
    }
}
