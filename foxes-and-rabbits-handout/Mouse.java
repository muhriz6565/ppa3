import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a mouse.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Mouse extends Animal
{
    // Characteristics shared by all mouses (class variables).

    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a mouse can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    private final boolean isMale = new Random().nextBoolean();

    // The mouse's age.
    private int age;

    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the mouse does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newMice A list to return newly born mouses.
     */
    public void act(List<Animal> newMice)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newMice);            
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the mouse's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMice A list to return newly born mouses.
     */
    private boolean findMates()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Mouse) {
                Mouse mouse = (Mouse) animal;
                if(mouse.isMale^this.isMale) { 
                    return true;
                }
            }
        }
        return false;
    }
    private void giveBirth(List<Animal> newMice)
    {
        // New mouses are born into adjacent locations.
        // Get a list of adjacent free locations.
        if (findMates()){
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Mouse young = new Mouse(false, field, loc);
                newMice.add(young);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }                                                           
        return births;
    }

    /**
     * A mouse can breed if it has reached the breeding age.
     * @return true if the mouse can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
