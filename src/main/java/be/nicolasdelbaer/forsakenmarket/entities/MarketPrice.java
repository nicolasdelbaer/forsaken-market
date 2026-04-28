package be.nicolasdelbaer.forsakenmarket.entities;

import be.nicolasdelbaer.forsakenmarket.enums.MarketTrend;
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
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter @Setter
    private Integer open;

    @Getter @Setter
    private Integer close;

    @Getter @Setter
    private Integer high;

    @Getter @Setter
    private Integer low;


    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private MarketTrend marketTrend;



    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "item_blueprint_id")
    private ItemBlueprint itemBlueprint;

    @Getter @Setter
    private Long roundId;

    @Getter @Setter
    private LocalDateTime createdAt;

    public Integer getCurrentPrice() {
        return close;
    }
}
