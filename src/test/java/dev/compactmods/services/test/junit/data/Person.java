package dev.compactmods.services.test.junit.data;

import com.github.javafaker.Faker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Person {

    public static final Codec<Person> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("name").forGetter(Person::name),
            Codec.STRING.fieldOf("sex").forGetter(Person::sex),
            Codec.INT.fieldOf("age").forGetter(Person::age)
    ).apply(i, Person::new));

    private String name;
    private String sex;
    private int age;

    public Person(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    public static Person random() {
        final var p = new Person("", "", 0);
        p.randomize();
        return p;
    }

    public String name() { return this.name; }
    public String sex() { return this.sex; }
    public int age() { return this.age; }

    public void randomize() {
        final var f = new Faker();
        this.name = f.name().fullName();
        this.sex = f.demographic().sex();
        this.age = f.number().numberBetween(18, 120);
    }
}
