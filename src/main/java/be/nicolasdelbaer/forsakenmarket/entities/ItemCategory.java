package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.*;

import java.util.Objects;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @Getter @Setter
    @Column(nullable = false)
    private String name;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private ItemCategory parentCategory;

    public ItemCategory(String name, ItemCategory category) {
        this.name = name;
        if(Objects.nonNull(category))
            this.parentCategory = category;
    }
}