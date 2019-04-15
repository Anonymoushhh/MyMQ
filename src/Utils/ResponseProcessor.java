package Utils;

import java.nio.channels.SelectionKey;

import Broker.Broker;

public interface ResponseProcessor {
	default void processorRespone(final SelectionKey key) {}
	default void processorRespone(final SelectionKey key,Broker broker) {}
	default void processorRespone(final SelectionKey key,int port) {}
}
