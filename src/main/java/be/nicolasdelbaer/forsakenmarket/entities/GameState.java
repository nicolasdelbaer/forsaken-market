package be.nicolasdelbaer.forsakenmarket.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
public class GameState {

    @Id
    @Getter
    private Integer id = 1; //Always 1 id only

    @Getter @Setter
    private Long currentRound;

    public GameState(Long currentRound) {
        this.currentRound = currentRound;
    }
}
