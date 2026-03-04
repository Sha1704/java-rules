package app;

import java.util.*;
import java.util.logging.Logger;

public class Dealer{
    private Hand hand;

    //constructor for empty hand
    public Dealer(){
        hand = new Hand();
    }

    public void addCard(Card card){
        if(card == null){
            return;
        }
        hand.addCard(card);
    }

    //get card total
    public int getHandValue(){
        return hand.getValue();
    }

    //show one card - Dealer only shows one card to the player
    public Card showOneCard(){
        if(hand.getCards().isEmpty()){
            return null;
        }
        return hand.getCards().get(0);
    }

    //get all card in hand - list
    public List<Card> getHandCards(){
        return List.copyOf(hand.getCards());
    }

    //reset hand for new round
    public void resetHand(){
        hand = new Hand();
    }

    //Dealer hits until hand value is 17 or more
    public void playTurn(Deck deck){
        while(getHandValue() < 17){
            Card newCard = deck.drawCard();
            if(newCard != null){
                addCard(newCard);
                Logger.getLogger(Dealer.class.getName()).info("Dealer hits and gets: " + newCard);
            } else {
                Logger.getLogger(Dealer.class.getName()).warning("Deck is empty, cannot deal more cards.");
                break;
            }
        }
        Logger.getLogger(Dealer.class.getName()).info("Dealer stands with hand value: " + getHandValue());
    }




}


