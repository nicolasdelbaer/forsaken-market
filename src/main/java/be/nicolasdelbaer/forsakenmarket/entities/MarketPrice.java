package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"item_blueprint_id", "round_id"}))
@NoArgsConstructor
@AllArgsConstructor
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter @Setter
    @Min(0)
    @Max(1_000_000)
    @Column(name = "current_price")
    private Integer currentPrice;

    /*
     * Previous is used for price evolution calculation algorithm
     */
    @Getter @Setter
    @Min(0)
    @Max(1_000_000)
    @Column(name = "previous_price")
    private Integer previousPrice;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    private ItemBlueprint itemBlueprint;

    @Getter @Setter
    @Column(name = "round_id")
    private Long roundId;

    @Getter @CreationTimestamp
    private LocalDateTime createdAt;

    public MarketPrice(Integer currentPrice, Integer previousPrice, ItemBlueprint itemBlueprint, Long roundId) {
        this.currentPrice = currentPrice;
        this.previousPrice = previousPrice;
        this.itemBlueprint = itemBlueprint;
        this.roundId = roundId;
    }

}
