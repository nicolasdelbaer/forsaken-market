package be.nicolasdelbaer.forsakenmarket.entities;

import be.nicolasdelbaer.forsakenmarket.enums.ItemRarity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class ItemBlueprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter @Setter
    private String title;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private Integer price;

    @Getter @Setter
    private String icon;

    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private ItemRarity rarity;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "item_category_id")
    private ItemCategory itemCategory;

    public ItemBlueprint(String title, String description, Integer price, String icon, ItemRarity rarity, ItemCategory itemCategory) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.icon = icon;
        this.rarity = rarity;
        this.itemCategory = itemCategory;
    }
}
