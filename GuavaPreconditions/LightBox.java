package pl.com.bottega.photostock.sales.model.usertool;

import pl.com.bottega.photostock.sales.model.products.Picture;
import pl.com.bottega.photostock.sales.model.users.Client;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static pl.com.bottega.photostock.sales.model.common.MathUtilits.getSymbolCount;
import static pl.com.bottega.photostock.sales.model.common.StringUtilites.buildSymbolLine;

public class LightBox implements Cloneable, Serializable {
    private String name;
    private String number = UUID.randomUUID().toString();
    private Client owner;
    private List<Picture> pictures = new LinkedList<>();
    private boolean isActive = true;
    private List<Client> admins = new LinkedList<>();


    public void setActive(boolean active) {
        isActive = active;
    }

    public LightBox(Client owner, String lightBoxName) {
        this.owner = owner;
        this.name = lightBoxName;
    }

    private LightBox(LightBox lightBox) {
        this.owner = lightBox.getOwner();
        this.name = lightBox.getName();
        this.pictures.addAll(lightBox.getPictures());
    }

    public LightBox(Client owner) {
        this.owner = owner;
    }

    public List<Client> getAdmins() {
        return admins;
    }

    @Override
    public LightBox clone() {
        LightBox result = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutput oo = new ObjectOutputStream(baos)) {
            oo.writeObject(this);
            oo.flush();
            byte[] object = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(object);
            ObjectInput oi = new ObjectInputStream(bais);
            result = (LightBox) oi.readObject();
        } catch (IOException e) {
            /*NOP*/
        } catch (ClassNotFoundException e) {
            /*NOP*/
        }
        return result;
    }

    public Client getOwner() {
        return owner;
    }

    public void clear() {
        pictures.clear();
    }

    public Picture getPictureByIndex(int index) {
        return pictures.get(index);
    }

    public Picture getPictureByNumber(String number) {
        for (Picture picture : pictures) {
            if (picture.getNumber().equals(number))
                return picture;
        }
        throw new IllegalArgumentException("Picture is not exist");
    }

    public void close() {
        isActive = false;
    }

    public void changeName(String newName) {
        validate();
        this.name = newName;
    }

    public void remove(Picture picture) {
        validate();
        checkArgument(pictures.remove(picture), "does not contain");
    }

    public void add(Picture picture) throws IllegalStateException, IllegalArgumentException {
        validate();
        checkState(!pictures.contains(picture), "already contains");
        checkState(picture.isAvailable(), "Now it's don't available!");
        pictures.add(picture);
    }

    private void validate() {
        checkState(isActive, "Light box is closed!");
        checkState(owner.isActive(), "owner is not active!");
    }

    public String getName() {
        return name;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        int iterator = 1;
        for (Picture picture : pictures) {

            if (iterator == 1)
                s.append(buildSymbolLine(getLongOfLongestString(), "=")).append("\n");

            s.append("| ").append(iterator).append(buildSymbolLine(getLongOfLongestNumber() - getSymbolCount(iterator), " ")).append(" | ");
            s.append(picture.getName()).append(buildSymbolLine(getLongOfLongestName() - picture.getName().length(), " ")).append(" | ");
            s.append(this.owner.getName()).append(" | ");

            if (picture.isAvailable()) {
                s.append(picture.getNumber()).append(buildSymbolLine(getLongOfLongestNumberPic() - picture.getNumber().length(), " ")).append(" | ");
                s.append(12).append(buildSymbolLine(getLongOfLongestPrice() - (String.valueOf(12).length()), " ")).append(" |").append("\n");
            } else {
                s.append("X").append(buildSymbolLine(getLongOfLongestNumberPic() - 1, " ")).append(" | ");
                s.append(12).append(buildSymbolLine(getLongOfLongestPrice() - (String.valueOf(12).length()), " ")).append(" |").append("\n");
            }
            String e = s.toString();
            s.append(buildSymbolLine(getLongOfLongestString(), "=")).append("\n");

            iterator++;
        }
        return s.toString();

    }

    private int getLongOfLongestString() {
        return getLongOfLongestNumber() + getLongOfLongestName() + getLongOfLongestOwner() + getLongOfLongestNumberPic() + getLongOfLongestPrice() + 16;
    }

    private int getLongOfLongestName() {
        int lenght = 0;
        for (Picture picture : pictures) {

            String name = picture.getName();
            int nameLength = name.length();

            if (nameLength > lenght)
                lenght = nameLength;
        }
        return lenght;
    }

    private int getLongOfLongestOwner() {
        String s = owner.getName();
        return s.length();
    }

    private int getLongOfLongestNumberPic() {
        int lenght = 0;
        for (Picture picture : pictures) {

            String name = picture.getNumber();
            int nameLength = name.length();

            if (nameLength > lenght)
                lenght = nameLength;
        }
        return lenght;
    }

    private int getLongOfLongestPrice() {
        int lenght = 0;
        for (Picture picture : pictures) {

            String name = String.valueOf(12);
            int nameLength = name.length();

            if (nameLength > lenght)
                lenght = nameLength;
        }
        return lenght;
    }

    private int getLongOfLongestNumber() {
        return getSymbolCount(pictures.size());
    }

    public String getNumber() {
        return number;
    }

    public void setCoOwner(Client coOwner) {
        this.admins.add(coOwner);
    }

    public String[] export() {
        String name = getName();
        String number = getNumber();
        String owner = getOwner().getNumber();
        String pictures = getProductsString();
        String isActive = String.valueOf(this.isActive);
        String admins = getAdminsString();

        return new String[]{name, number, owner, pictures, isActive, admins};
    }

    private String getProductsString() {
        if (getPictures().size() == 0)
            return "";
        StringBuilder builder = new StringBuilder();
        for (Picture picture : pictures) {
            builder.append(picture.getNumber());
            builder.append("|");
        }
        return builder.toString();
    }

    private String getAdminsString() {
        if (getAdmins().size() == 0)
            return "";
        StringBuilder builder = new StringBuilder();
        for (Client admin : admins) {
            builder.append(admin.getNumber());
            builder.append("|");
        }
        return builder.toString();
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
