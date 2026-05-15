package model;

/**
 * Клас, що представляє Шолом як частину обладунків.
 * Демонструє успадкування від Armor.
 */
public class Helmet extends Armor {

    public Helmet(String name, double weight, double price, int defense) {
        super(name, weight, price, defense);
    }

    @Override
    public String toString() {
        return "Helmet: " + super.toString() + ", defense=" + getDefense();
    }
}
