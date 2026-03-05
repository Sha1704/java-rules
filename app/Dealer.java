package app;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Dealer {
    private Hand hand;
    // VNA00-J: volatile variable for visibility across threads (moved from Player)
    // Now used to track dealer's state across dealer and player threads
    private volatile boolean isPlaying;
    private volatile String gameState;
    
    // Thread-safe map for multiple players
    private final Map<String, Player> players; 
    private final Deck deck;
    private static final Logger LOGGER = Logger.getLogger(Dealer.class.getName());

    /**
     * Constructor for Dealer with a given deck. Initializes the dealer's hand and player list.
     * @param deck
     */
    public Dealer(Deck deck) {
        this.hand = new Hand();
        // Thread safe list (moved from Player)
        this.players = new ConcurrentHashMap<>(); // Thread-safe for multiple player access
        this.deck = Objects.requireNonNull(deck, "Deck cannot be null");
        this.isPlaying = false;
        this.gameState = "WAITING";
    }

    /**
     * Add a player to the game
     * @param player - player to add
     */
    public void addPlayer(Player player) {
        // ERR08-J: Prevent NullPointerExceptions by checking parameters before use (moved from Player)
        Objects.requireNonNull(player, "Player cannot be null");
        players.put(player.getPlayerId(), player);
        LOGGER.info("Player " + player.getName() + " added to the game");
    }

    /**
     * Remove a player from the game
     * @param playerId - ID of player to remove
     */
    public void removePlayer(String playerId) {
        Player removed = players.remove(playerId);
        if (removed != null) {
            LOGGER.info("Player " + removed.getName() + " removed from the game");
        }
    }

    /**
     * Get all players
     * @return collection of all players
     */
    public Collection<Player> getAllPlayers() {
        // MET52-J: Return a defensive copy of the list (moved from Player)
        return new ArrayList<>(players.values());
    }

    /**
     * Start a new round
     * VNA00-J: volatile variable ensures game state changes are visible across threads
     */
    public void startNewRound() {
        // Reset dealer's hand
        resetHand();
        isPlaying = true;
        gameState = "DEALING";
        
        // Deal initial cards to players
        for (Player player : players.values()) {
            if (player.isActive()) {
                // Deal initial two cards to player
                Card card1 = deck.drawCard();
                Card card2 = deck.drawCard();
                player.dealCard(card1);
                player.dealCard(card2);
                LOGGER.info("Dealt cards to " + player.getName());
            }
        }
        
        // Deal one card to dealer (face up)
        Card dealerCard = deck.drawCard();
        addCard(dealerCard);
        LOGGER.info("Dealer shows: " + dealerCard);
        gameState = "PLAYER_TURNS";
    }

    /**
     * Process player turns
     * VNA00-J: volatile variable ensures player turn state is visible
     * ERR04-J: Do not complete abruptly from a finally block - InterruptedException
     * is handled properly by restoring the interrupted status
     */
    public void processPlayerTurns() {
        for (Player player : players.values()) {
            if (player.hasActiveHand()) {
                gameState = "WAITING_FOR_" + player.getPlayerId();
                // VNA00-J: volatile ensures this update is visible to player threads
                
                try {
                    // Wait for player decision with timeout
                    long timeout = 30000; // 30 second timeout
                    long startTime = System.currentTimeMillis();
                    
                    while (!player.isReadyForAction() && 
                        (System.currentTimeMillis() - startTime) < timeout) {
                        // Wait for player to make decision
                        Thread.sleep(100);
                    }
                    
                    if (player.isReadyForAction()) {
                        // Process player action
                        Player.PlayerAction action = player.getCurrentAction();
                        if (action == Player.PlayerAction.HIT) {
                            Card newCard = deck.drawCard();
                            player.dealCard(newCard);
                            LOGGER.info(player.getName() + " hits and gets: " + newCard);
                        } else if (action == Player.PlayerAction.STAND) {
                            player.getHand().setStanding(true);
                            LOGGER.info(player.getName() + " stands");
                        }
                        player.resetReadyForAction();
                    } else {
                        // Player timeout
                        LOGGER.info(player.getName() + " timed out - auto-stand");
                        player.getHand().setStanding(true);
                    }
                } catch (InterruptedException e) {
                    // ERR04-J: Do not complete abruptly from a finally block
                    // Instead of swallowing the exception, restore interrupted status
                    Thread.currentThread().interrupt();
                    // ERR04-J-EX0: Allows complete abruptly from a control flow statement
                    LOGGER.warning("Player turn processing interrupted for " + player.getName());
                    // Continue processing other players instead of abruptly completing
                }
            }
        }
        gameState = "DEALER_TURN";
    }

    /**
     * Add a card to the dealer's hand
     * @param card
     */
    public void addCard(Card card) {
        // ERR08-J: Prevent NullPointerExceptions (moved from Player)
        if(card == null) {
            return;
        }
        hand.addCard(card);
    }

    /**
     * Get the total value of the dealer's hand
     * @return total hand value
      */
    public int getHandValue() {
        return hand.getValue();
    }

    /**
     * Show one card of the dealer's hand (the face-up card)
     * @return the face-up card or null if hand is empty
     */
    public Card showOneCard() {
        if(hand.getCards().isEmpty()) {
            return null;
        }
        return hand.getCards().get(0);
    }

    /**
     * Get all cards in the dealer's hand
     * @return list of cards in dealer's hand
     */
    public List<Card> getHandCards(){
      // MET52-J: Return a defensive copy (moved from Player)
        return hand.getCards();
    }

    /**
     * Reset the dealer's hand for a new round
     */
    public void resetHand() {
        hand = new Hand();
    }

    /**
     * Play the dealer's turn according to standard blackjack rules (hit until 17 or higher).
     */
    public void playTurn() {
        while(getHandValue() < 17) {
            Card newCard = deck.drawCard();
            if(newCard != null) {
                addCard(newCard);
                LOGGER.info("Dealer hits and gets: " + newCard);
            } else {
                LOGGER.warning("Deck is empty, cannot deal more cards.");
                break;
            }
        }
        LOGGER.info("Dealer stands with hand value: " + getHandValue());
        gameState = "SETTLING";
    }

    /**
     * Settle bets after round is complete
     */
    public void settleBets() {
        int dealerValue = getHandValue();
        boolean dealerBusted = dealerValue > 21;
        
        for (Player player : players.values()) {
            Hand playerHand = player.getHand();
            if (playerHand == null) continue;
            
            int playerValue = playerHand.getValue();
            
            if (playerHand.isBusted()) {
                // Player already lost
                LOGGER.info(player.getName() + " busted - loses");
            } else if (dealerBusted) {
                // Dealer busted, player wins
                LOGGER.info(player.getName() + " wins - dealer busted");
            } else if (playerValue > dealerValue) {
                // Player beats dealer
                LOGGER.info(player.getName() + " wins with " + playerValue);
            } else if (playerValue == dealerValue) {
                // Push
                LOGGER.info(player.getName() + " pushes");
            } else {
                // Player loses
                LOGGER.info(player.getName() + " loses");
            }
        }
        isPlaying = false;
        gameState = "ROUND_COMPLETE";
    }

    /**
     * Reset all players for a new round
     */
    public void resetAllPlayers() {
        for (Player player : players.values()) {
            player.resetForNewRound();
        }
    }

    //Getters and setters with defensive copies
    public boolean isPlaying() {
        // VNA00-J: volatile ensures current value is returned
        return isPlaying;
    }

    public String getGameState() {
        // VNA00-J: volatile ensures current value is returned
        return gameState;
    }

    @Override
    public String toString() {
        return String.format("Dealer{hand=%s, players=%d, playing=%b, state=%s}", 
        hand.getCards(), players.size(), isPlaying, gameState);
    }
}