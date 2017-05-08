package annoy34.imap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class ImapQuery {

	public static final String EMAIL_ADDRESS = "";
	public static final String EMAIL_PASSWORD = "";

	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			Store store = getStore();
			Folder inbox = getInbox(store);

			List<Address> froms = getFroms(inbox);
			/*
			 * for (Address address : froms) {
			 * System.out.println(address.toString()); }
			 */

			store.close();
			System.out.println(System.currentTimeMillis() - start);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Store getStore() throws MessagingException {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Store store = null;
		try {
			Session session = Session.getDefaultInstance(props, null);
			store = session.getStore("imaps");
			store.connect("imap.gmx.de", EMAIL_ADDRESS, EMAIL_PASSWORD);
			return store;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return store;
	}

	public static Folder getInbox(Store store) throws MessagingException {
		Folder folder = store.getFolder("INBOX");
		return folder;
	}

	public static List<Address> getFroms(Folder folder) throws MessagingException {
		List<Address> fromAddresses = new ArrayList<Address>();
		folder.open(Folder.READ_ONLY);
		try {
			Message[] msgs = folder.getMessages();

			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);

			folder.fetch(msgs, fp); // bulk pre-fetch envelope attributes

			for (Message msg : msgs) {
				Address[] from = msg.getFrom();
				if (from != null) { // from can be null if no from in envelope
					fromAddresses.addAll(Arrays.asList(from));
				}
			}

			for (Folder subFolder : folder.list()) {
				fromAddresses.addAll(getFroms(subFolder));
			}
		} catch (MessagingException e) {
			throw e;
		} finally {
			folder.close(false);
		}
		return fromAddresses;
	}

}
