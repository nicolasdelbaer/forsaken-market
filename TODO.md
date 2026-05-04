# TODO LIST

## MVP content
### User login
- [x] register as player
- [x] login as player
- [x] logout as player

### User interactions
- [x] player won reput when selling
- [x] player can buy item to their inventory
- [x] player can sell item from their inventory
- [x] player can reroll an item from the market for a price

### Cycle & game state related
- [x] populate items to market
- [x] handle schedule ticks on server
- [x] items will decay
- [x] items will get out of market
- [x] update market OCHL after a cycle of rounds

### Track items price movement
- [x] update prices for the current round
- [ ] store movements (player buy, sell, ... actions)
- [ ] retrieve movements (player buy, sell, ... actions)




## TO DO NEXT

### Market
- Clean old MarketPrice references & change name for OCHL

### Meta
- Sync server & client (send date for auto refresh + data)
- Send data for player exp/reput & display
- Rework responses content (if cost, send back wallet data and so on)

### Project
- Update & Clean auth filter flow (access roles annotation rather than public)

### Game Rules
- Decay must be very high & based on rarity (utils)
- Time to live based on ranged & rarity (utils)




## TO KEEP IN MIND
### Refacto
- Code & project review
- Clean InventoryItem responsibility cf player movements?

### DTO
- Check if needs of DTO for requests & queries
- Add validations via annotations @NotBlank, length etc. from incoming data

### Game State
- Add Persistence ->table with 1 id and store current round & stuff

### Testing
- Add new test cases

### Update theory & must known

### Optimisation
- Review db accesses & server cached data ()