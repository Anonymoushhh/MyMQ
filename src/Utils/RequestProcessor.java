package Utils;

import java.nio.channels.SelectionKey;

public interface RequestProcessor {
	public void processorRequest(final SelectionKey key);
}
