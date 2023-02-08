/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onefoursix;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSInotifyEventInputStream;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.inotify.Event.CreateEvent;
import org.apache.hadoop.hdfs.inotify.Event.UnlinkEvent;
import org.apache.hadoop.hdfs.inotify.EventBatch;
import org.apache.hadoop.hdfs.inotify.MissingEventsException;

public class HdfsINotifyExample {

	public static void main(String[] args) throws IOException, InterruptedException, MissingEventsException {

		long lastReadTxid = 0;

		if (args.length > 1) {
			lastReadTxid = Long.parseLong(args[1]);
		}

		System.out.println("lastReadTxid = " + lastReadTxid);

		HdfsAdmin admin = new HdfsAdmin(URI.create(args[0]), new Configuration());

		DFSInotifyEventInputStream eventStream = admin.getInotifyEventStream(lastReadTxid);

		while (true) {
			EventBatch batch = eventStream.take();
			System.out.println("TxId = " + batch.getTxid());

			for (Event event : batch.getEvents()) {
				System.out.println("event type = " + event.getEventType());
				switch (event.getEventType()) {
				case CREATE:
					CreateEvent createEvent = (CreateEvent) event;
					System.out.println("  path = " + createEvent.getPath());
					System.out.println("  owner = " + createEvent.getOwnerName());
					System.out.println("  ctime = " + createEvent.getCtime());
					break;
				case UNLINK:
					UnlinkEvent unlinkEvent = (UnlinkEvent) event;
					System.out.println("  path = " + unlinkEvent.getPath());
					System.out.println("  timestamp = " + unlinkEvent.getTimestamp());
					break;
				
				case APPEND:
				case CLOSE:
				case RENAME:
				default:
					break;
				}
			}
		}
	}
}

