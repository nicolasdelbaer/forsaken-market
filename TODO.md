# TODO LIST

## MVP content
### User login
- register as player
- login as player
- logout as player

### User interactions
- player won exp when selling
- player can buy item to their inventory
- player can sell item from their inventory
- player can reroll an item from the market for a price

### Cycle & game state related
- populate items to market
- handle schedule ticks on server
- items will decay
- items will get out of market

### Track items price movement
- update prices for the current round
- store movements (player buy & sell actions)
- retrieve movements (player buy & sell actions)



## TO DO NEXT
### Market
- Reroll items
- ? Filtered Market item list
- Market price have to progress and update trends
- Fetch range of price evolution for display

### Project
- Code & project review


## TO KEEP IN MIND
### Refacto
- Split MarketService to MarketService & ScheduleMarketService for clarity
- Clean BoughtItem responsability cf player movements.

### DTO
- Check if needs of DTO for requests & queries
- Ajouter les validations via annotations @NotBlank, length etc.
- Utiliser PlayerResponse comme container du User

### Game State
- Add a dedicated table with 1 id and store current round & stuff

### Testing
- Create & add test cases

### Auth
- Update & Clean auth filter flow

### Update theory & must known
- Update notion (Filters, Annotations, Auth, Scope & Beans, Scheduler lifecycle, Interceptor, Producer, Jql joins)


### Features
- Test feature as a user with basic front view
