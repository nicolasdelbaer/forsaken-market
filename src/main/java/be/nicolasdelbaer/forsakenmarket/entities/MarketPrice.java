package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table

/*
* Trade of sur la compatibilité du array_agg permettant de tout faire en une requête plus opti
*/
@NamedNativeQuery(name = "MarketPrice.ohlc", query = """
SELECT 
    item_blueprint_id,
    (array_agg(mp.currentPrice order by roundid ASC))[1] as open,
    max(currentPrice) as high,
    min(currentPrice) as low,
    (array_agg(mp.currentPrice order by roundid DESC))[1] as close
FROM marketprice
WHERE roundid BETWEEN :startId AND :endId
GROUP BY item_blueprint_id
""")

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

    @Getter @Setter
    private LocalDateTime createdAt;

    public MarketPrice(Integer currentPrice, ItemBlueprint itemBlueprint, Long roundId, LocalDateTime createdAt) {
        this.currentPrice = currentPrice;
        this.itemBlueprint = itemBlueprint;
        this.roundId = roundId;
        this.createdAt = createdAt;
    }
}
