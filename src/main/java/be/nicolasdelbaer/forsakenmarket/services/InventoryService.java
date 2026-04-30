package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.BadItemOwnershipException;
import be.nicolasdelbaer.forsakenmarket.exceptions.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.CannotDiscardItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.CannotUpdateDecayOnItemException;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemDto;
import be.nicolasdelbaer.forsakenmarket.repositories.BoughtItemRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class InventoryService {

    @Inject private BoughtItemRepository boughtItemRepository;
    @Inject private EntityManager entityManager;
    @Inject private GameState gameState;

    @Transactional
    public void acquireItem(BuyItemDto buyItemDto){
        Integer decayTime = 8; //TODO calculte right round nb time

        BoughtItem boughtItem = new BoughtItem();
        boughtItem.setItemBlueprint(buyItemDto.marketItem().getItemBlueprint());
        boughtItem.setMarketItemId(buyItemDto.marketItem().getId());
        boughtItem.setPlayer(buyItemDto.player());

        boughtItem.setBoughtPrice(buyItemDto.marketPrice().getCurrentPrice());
        boughtItem.setBoughtAt(LocalDateTime.now());
        boughtItem.setStatus(MarketItemStatus.BOUGHT);

        boughtItem.setDecayNbRounds(decayTime);
        boughtItem.setBoughtRoundId(buyItemDto.roundId());

        boughtItemRepository.save(entityManager, boughtItem);
    }

    //Active bought items must be sold to get added value before they get decayed
    @Transactional
    public void sellItem(BoughtItem boughtItem, Long currentRound) throws CannotSellInactiveItemException {
        if(boughtItem.getStatus() != MarketItemStatus.BOUGHT)
            throw new CannotSellInactiveItemException("The item is already decayed, sold or thrown away");
        boughtItem.setSoldAt(LocalDateTime.now());
        boughtItem.setStatus(MarketItemStatus.SOLD);
        boughtItem.setSoldRoundId(currentRound);
        boughtItemRepository.save(entityManager, boughtItem);
    }

    //When an item is decayed you have to thrown it away, you cannot sell it anymore and it'll occupy an inventory slot
    @Transactional
    public void discardItem(Integer playerId, Long itemId) throws CannotDiscardItemException {
        //Note, the current round id is resolved here for keeping coherence
        // Idea -> could use a window of tolerance in the future allowing players to get the item even with lags
        Long currentRound = gameState.getCurrentRound();

        BoughtItem boughtItem = boughtItemRepository
                .getItemFromPlayer(entityManager, itemId, playerId, MarketItemStatus.DECAYED)
                .orElseThrow(() -> new CannotDiscardItemException("Invalid item or unauthorized access"));

        boughtItem.setDiscardedAt(LocalDateTime.now());
        boughtItem.setStatus(MarketItemStatus.DISCARDED);
        boughtItem.setDiscardedRoundId(currentRound);
        boughtItemRepository.save(entityManager, boughtItem);
    }
}
