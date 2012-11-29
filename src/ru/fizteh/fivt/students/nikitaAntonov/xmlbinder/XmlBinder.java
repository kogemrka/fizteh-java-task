package ru.fizteh.fivt.students.nikitaAntonov.xmlbinder;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {

    public XmlBinder(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public byte[] serialize(T value) {
    }

    @Override
    public T deserialize(byte[] bytes) {
    }
}
