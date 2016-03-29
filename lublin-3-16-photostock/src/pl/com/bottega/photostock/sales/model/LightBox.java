package pl.com.bottega.photostock.sales.model;

import java.util.ArrayList;
import java.util.List;

import static pl.com.bottega.photostock.sales.model.Utilits.buildSymbolLine;
import static pl.com.bottega.photostock.sales.model.Utilits.getSymbolCount;

/**
 * Created by Slawek on 12/03/16.
 */
public class LightBox {

    private String name;
    private Client owner;
    private List<Picture> items = new ArrayList<>();
    private boolean closed = false;

    public LightBox(Client owner) {
        this.owner = owner;
    }

    public void close() {
        this.closed = true;
    }

    public void changeName(String newName) {
        validate();
        this.name = newName;
    }

    /*
    public - modyfukiator dostępu
    static - opcjonanie aby metoda na rzecz klasy a nie obiektou
    void - typ zwracany, bez zwracania
    remove - nazwenik metody
    () - parametry
          Picture - typ
          pictureToRemove - nazewnik parametru
     */
    public void remove(Picture pictureToRemove) {
        validate();
        boolean removed = items.remove(pictureToRemove);
        if (!removed)
            throw new IllegalArgumentException("does not contain");
    }

    public void add(Picture pictureToAdd) throws IllegalStateException, IllegalArgumentException {
        validate();
        if (items.contains(pictureToAdd))
            throw new IllegalArgumentException("already contains");
        items.add(pictureToAdd);
    }

    private void validate() {
        if (closed)
            throw new IllegalStateException("closed!");
        if (!owner.isActive())
            throw new IllegalStateException("owner is not active!");
    }

    /*
        public void add2(Picture pictureToAdd) throws IllegalStateException, IllegalArgumentException{
            //sprawdzamy czy items zawiera już ten picture
            //for (int coursor = 0; coursor < items.length; coursor++){
            for(Picture pic : items){
                //Picture pic = items[coursor];
                if (pic != null){
                    String nr1 = pic.getNumber();
                    String nr2 = pictureToAdd.getNumber();
                    if (nr1.equals(nr2)){
                        throw new IllegalArgumentException("lightbox already contains picture " + pictureToAdd.getNumber());
                    }
                }
            }
            //dodaje go jezeli znajdzie puste miejsce

            boolean added = false;
            int coursor = 0;
            //for (int coursor = 0; coursor < items.length; coursor++){
            for(Picture reference : items){
                //Picture reference = items[coursor];
                if (reference == null){
                    //reference = pictureToAdd; inne miejsce niż krakta tablicy!
                    items[coursor] = pictureToAdd;
                    added = true;
                    break;//break  !!!!!
                }
                coursor++;
            }
            if(!added)
                throw new IllegalStateException("Lightbox przepełniony!!!");
        }
    */
    public String getName() {
        return name;
    }

    public int getItemsCount() {
        return items.size();
    }

    public List<Picture> getPictures() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        int iterator = 1;
        for (Picture pic : items) {

            if (iterator == 1)
                s.append(buildSymbolLine(getLognOfLongestString(), "=")).append("\n");

            s.append("| ").append(iterator).append(buildSymbolLine(getLognOfLongestNumber() - getSymbolCount(iterator), " ")).append(" | ");
            s.append(pic.getName()).append(buildSymbolLine(getLognOfLongestName() - pic.getName().length(), " ")).append(" | ");
            s.append(this.owner.getName()).append(" | ");

            if (pic.isAvailable()) {
                s.append(pic.getNumber()).append(buildSymbolLine(getLongOfLongestNumberPic() - pic.getNumber().length(), " ")).append(" | ");
                s.append(pic.getPrice()).append(buildSymbolLine(getLongOfLongestPrice() - (String.valueOf(pic.getPrice()).length()), " ")).append(" |").append("\n");
            } else {
                s.append("X").append(buildSymbolLine(getLongOfLongestNumberPic() - 1, " ")).append(" | ");
                s.append(pic.getPrice()).append(buildSymbolLine(getLongOfLongestPrice() - (String.valueOf(pic.getPrice()).length()), " ")).append(" |").append("\n");
            }
            String e = s.toString();
            s.append(buildSymbolLine(getLognOfLongestString(), "=")).append("\n");

            iterator++;
        }
        return s.toString();

    }

    public int getLognOfLongestString() {
        return getLognOfLongestNumber() + getLognOfLongestName() + getLongOfLongestOwner() + getLongOfLongestNumberPic() + getLongOfLongestPrice() + 16;
    }

    public int getLognOfLongestName() {
        int lenght = 0;
        for (Picture pic : items) {

            String name = pic.getName();
            int nameLength = name.length();

            if (nameLength > lenght)
                lenght = nameLength;
        }
        return lenght;
    }

    public int getLongOfLongestOwner() {
        String s = owner.getName();
        return s.length();
    }

    public int getLongOfLongestNumberPic() {
        int lenght = 0;
        for (Picture pic : items) {

            String name = pic.getNumber();
            int nameLength = name.length();

            if (nameLength > lenght)
                lenght = nameLength;
        }
        return lenght;
    }

    public int getLongOfLongestPrice() {
        int lenght = 0;
        for (Picture pic : items) {

            String name = String.valueOf(pic.getPrice());
            int nameLength = name.length();

            if (nameLength > lenght)
                lenght = nameLength;
        }
        return lenght;
    }

    public int getLognOfLongestNumber() {
        return getSymbolCount(items.size());
    }
}
