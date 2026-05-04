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
- [ ] player can reroll an item from the market for a price

### Cycle & game state related
- [x] populate items to market
- [x] handle schedule ticks on server
- [ ] items will decay
- [x] items will get out of market
- [ ] market cycle ends after 20 rounds

### Track items price movement
- [x] update prices for the current round
- [ ] store movements (player buy & sell actions)
- [ ] retrieve movements (player buy & sell actions)




## TO DO NEXT

### Market
- Market price have to progress and update trends
- Market price as single value
- Keep prices in server cache and persist cycles prices info (20 rounds)
- Clean old MarketPrice references & change name for OCHL
- Fetch range of price evolution for display
- Reroll items (crash)

### Meta
- Sync server & client (send date for auto refresh + data)
- Send data for player exp/reput & display

### Project
- Update & Clean auth filter flow

### Game Rules
- Decay must be very high & based on rarity (utils)
- Time to live based on ranged & rarity (utils)




## TO KEEP IN MIND
### Refacto
- Code & project review
- Clean BoughtItem responsibility cf player movements?

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