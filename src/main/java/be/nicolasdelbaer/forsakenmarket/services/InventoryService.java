package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemDto;
import be.nicolasdelbaer.forsakenmarket.repositories.BoughtItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@ApplicationScoped
public class InventoryService {

    @Inject
    private BoughtItemRepository boughtItemRepository;

    public void addToInventory(BuyItemDto buyItemDto){
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

        boughtItemRepository.save(boughtItem);
    }

    //TODO clean old refs on schedule?
    public void removeFromInventory(BoughtItem boughtItem, Long currentRound){
        boughtItem.setSoldAt(LocalDateTime.now());
        boughtItem.setStatus(MarketItemStatus.SOLD);
        boughtItem.setSoldRoundId(currentRound);
        boughtItemRepository.save(boughtItem);
    }
}
