package pl.com.bottega.commons.math.utilits.func;


import org.junit.Test;
import pl.com.bottega.photostock.sales.model.deal.Money;
import pl.com.bottega.photostock.sales.model.products.Picture;

import java.util.function.Function;

import static org.junit.Assert.*;
import static pl.com.bottega.photostock.sales.model.deal.Money.Currency.USD;


public class NonEmptyListTest {

    private Picture picture_0 = new Picture("Name0", "nr0", new Money(200.0, USD), true, "Tag1", "Tag2");
    private Picture picture_1 = new Picture("Name1", "nr1", new Money(100.0, USD), false, "Tag1", "Tag2", "Tag3");
    private Picture picture_2 = new Picture("Name2", "nr2", new Money(50.0, USD), true, "Tag1", "Tag2", "Tag4");
    private Picture picture_3 = new Picture("Name3", "nr3", new Money(50.0, USD), true, "Tag1", "Tag2", "Tag4");
    private Picture picture_4 = new Picture("Name4", "nr4", new Money(50.0, USD), true, "Tag1", "Tag2", "Tag4");

    @Test
    public void shouldAddElements() {
        //given
        FunList<String> words = FunList.create();

        //when
        words = words.add("first").add("second").add("third").add("fifth");

        //then
        assertTrue(words.contains("first"));
        assertTrue(words.contains("third"));
    }

    @Test
    public void shouldReturnStringRepresentation() {
        //given
        FunList<String> words = FunList.create();

        //when
        words = words.add("first").add("second").add("third").add("fifth");

        //then
        assertEquals("first, second, third, fifth", words.toString());
    }

    @Test
    public void shouldReturnSize() {
        //given
        FunList<String> words = FunList.create();

        //when
        words = words.add("first").add("second").add("third").add("fifth");

        //then
        assertEquals(4, words.size());
    }

    @Test
    public void shouldGetElement() {
        //given
        FunList<String> words = FunList.create();

        //when
        words = words.add("first").add("second").add("third").add("fifth");

        //then
        assertNull(words.get(-1));
        assertNull(words.get(10000000));
        assertEquals("second", words.get(1));
    }

    @Test
    public void shouldFindElement() {
        //given
        FunList<Integer> numbers = FunList.create();

        //when
        numbers = numbers.add(100).add(50).add(300).add(400);

        //then
        int number = numbers.find(i -> i > 200);

        assertEquals(300, number);
        assertNull(numbers.find(i -> i == 500));

        int number2 = numbers.find(i -> i == 400);

        assertEquals(400, number2);
        assertEquals(50, (int) numbers.find(i -> i == 50));
        assertNull(numbers.find(x -> x > 1000));
    }

    @Test
    public void shouldMapElements() {
        //given
        FunList<String> numbers = FunList.create();
        numbers = numbers.add("1").add("2").add("3").add("4").add("5").add("100");

        //when
        FunList<Integer> mapped = numbers.map(Integer::valueOf);
        FunList<Integer> mappedAnotherWay = numbers.map(Integer::valueOf);

        //then
        assertTrue(mapped.contains(1));
        assertTrue(mapped.contains(3));
        assertTrue(mappedAnotherWay.contains(4));
        assertTrue(mappedAnotherWay.contains(100));

        FunList<Integer> expected = FunList.create();
        expected = expected.add(1).add(2).add(3).add(4).add(5).add(100);

        assertEquals(expected, mapped);
        assertEquals(expected, mappedAnotherWay);
    }

    @Test
    public void shouldReduceElements() {
        //when
        FunList<Picture> pictures = FunList.create();
        pictures = pictures.add(picture_1).add(picture_2).add(picture_3);

        //when
        int availablePicturesCount = pictures.reduce(0, (accumulator, product) -> product.isAvailable() ? accumulator + 1 : accumulator);

        Money totalSum = pictures.reduce(new Money(0.0, USD), (accumulator, product) -> accumulator.add(product.calculatePrice()));

        FunList<String> tags = pictures.reduce(FunList.create(), (accumulator, picture) -> {
            for (String tag : picture.getTags())
                if (!accumulator.contains(tag))
                    accumulator = accumulator.add(tag);
            return accumulator;
        });

        //then
        assertEquals(availablePicturesCount, 2);
        assertEquals(new Money(200.0, USD), totalSum);

        FunList<String> expectedTags = FunList.create();
        expectedTags = expectedTags.add("Tag1").add("Tag2").add("Tag3").add("Tag4");

        assertEquals(expectedTags, tags);
    }

    @Test
    public void shouldCheckEqualsLists() {
        FunList<Picture> funList_1 = FunList.create();
        FunList<Picture> funList_2 = FunList.create();
        assertEquals(funList_1, funList_2);
    }

    @Test
    public void shouldCheckNotEqualsLists() {
        //given
        FunList<Picture> emptyFunList = FunList.create();
        FunList<Picture> funListWithOneElement = FunList.create();

        //when
        funListWithOneElement = funListWithOneElement.add(picture_1);

        //then
        assertNotEquals(emptyFunList, funListWithOneElement);
    }

    @Test
    public void shouldRemoveElem() {
        //given
        FunList<Picture> pictures = FunList.create();
        pictures = pictures.add(picture_0).add(picture_1).add(picture_2).add(picture_3).add(picture_4);

        //when
        pictures = pictures.remove(picture_2);

        FunList<Picture> exceptedPictures = FunList.create();
        exceptedPictures = exceptedPictures.add(picture_0).add(picture_1).add(picture_3).add(picture_4);

        //then
        assertEquals(exceptedPictures, pictures);
    }

    @Test
    public void shouldFilterFunList() {
        //given
        FunList<Picture> pictures = FunList.create();
        pictures = pictures.add(picture_0).add(picture_1).add(picture_2).add(picture_3).add(picture_4);

        //when
        pictures = pictures.filter(picture -> picture.calculatePrice().equals(new Money(50.0, USD)));
        FunList<Picture> controlList = FunList.create();
        controlList = controlList.add(picture_2).add(picture_3).add(picture_4);

        //then
        assertEquals(controlList, pictures);
    }

    @Test
    public void shouldDoSomeThinkWithEachElement() {
        //given
        FunList<Picture> pictures = FunList.create();
        pictures = pictures.add(picture_0).add(picture_1).add(picture_2).add(picture_3).add(picture_4);

        //when
        pictures.each(picture -> picture.setPrice(picture.calculatePrice().add(new Money(20.0, USD))));
        Money totalSum = pictures.reduce(new Money(0.0, USD), (accumulator, product) -> accumulator.add(product.calculatePrice()));

        //then
        assertEquals(new Money(550.0, USD), totalSum);
    }

    @Test
    public void shouldConcatenateLists() {
        //given
        FunList<Picture> sourceListOne = FunList.create();
        sourceListOne = sourceListOne.add(picture_0).add(picture_1).add(picture_2);

        FunList<Picture> sourceListTwo = FunList.create();
        sourceListTwo = sourceListTwo.add(picture_3).add(picture_4);

        //when
        FunList<Picture> result = sourceListOne.concat(sourceListTwo);
        FunList<Picture> controlList = FunList.create();
        controlList = controlList.add(picture_0).add(picture_1).add(picture_2).add(picture_3).add(picture_4);

        //then
        assertEquals(controlList, result);
    }

    @Test
    public void shouldGetSubList() {
        //given
        FunList<Picture> sourceList = FunList.create();
        sourceList = sourceList.add(picture_0).add(picture_1).add(picture_2).add(picture_3).add(picture_4);

        //when
        sourceList = sourceList.sublist(0, 1);
        FunList<Picture> controlList = FunList.create();
        controlList = controlList.add(picture_0).add(picture_1);

        //then
        assertEquals(controlList, sourceList);
    }

    @Test
    public void shouldGetSubListByWrongIndexes() {
        //given
        FunList<Picture> sourceList = FunList.create();
        sourceList = sourceList.add(picture_0).add(picture_1).add(picture_2).add(picture_3).add(picture_4);

        //when
        sourceList = sourceList.sublist(0, 25);
        FunList<Picture> controlList = FunList.create();

        //then
        assertEquals(controlList, sourceList);
    }

    @Test
    public void shouldGetSubListWithEmptyList() {
        //given
        FunList<Picture> sourceList = FunList.create();

        //when
        sourceList = sourceList.sublist(0, 1);
        FunList<Picture> controlList = FunList.create();

        //then
        assertEquals(controlList, sourceList);
    }
}