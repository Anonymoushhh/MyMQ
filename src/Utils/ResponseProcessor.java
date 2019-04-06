package Utils;

import java.nio.channels.SelectionKey;

public interface ResponseProcessor {
	public void processorRespone(final SelectionKey key) ;
}
