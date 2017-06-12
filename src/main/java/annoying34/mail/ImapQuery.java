package annoying34.mail;

import javax.mail.*;
import java.util.*;
import java.util.stream.Collectors;

public class ImapQuery extends MailAccessor {
    private Store store;
    private FetchProfile fetchProfile;

    public ImapQuery(String address, String password, String imapServer) throws ImapException {
        super(address, password);
        store = getStore(address, password, imapServer);
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fetchProfile = fp;
    }

    /**
     * Return a {@link Set} of all domains appearing in the given mails
     *
     * @param addresses A {@link Set} of {@link MailAddress} objects
     * @return A {@link Set} of domains
     */
    public static Set<String> getDomains(Set<MailAddress> addresses) {
        return addresses.stream().map(address -> address.getDomain()).collect(Collectors.toSet());
    }

    /**
     * Convert {@link Address} objects into a {@link Set} of {@link MailAddress} objects, filtering out invalid email addresses.
     *
     * @param adresses A list of {@link Address} objects
     * @return A {@link Set} of {@link MailAddress} objects, representing valid email addresses
     */
    private static Set<MailAddress> convertAdressesToMailAddresses(List<Address> adresses) {
        Set<MailAddress> mailAddresses = new HashSet<MailAddress>();
        for (Address address : adresses) {
            try {
                mailAddresses.add(new MailAddress(address));
            } catch (ImapException e) {
                // Mail address is invalid, don't add it
            }
        }

        return mailAddresses;
    }

    private Store getStore(String address, String password, String imapServer) throws ImapException {
        try {
            store = session.getStore("imaps");
            store.connect(imapServer, address, password);
            return store;
        } catch (NoSuchProviderException | AuthenticationFailedException e) {
            throw new ImapException(e.getMessage(), e);
        } catch (Exception e) {
            throw new ImapException("Error while trying to connect to IMAP server", e);
        }
    }

    /**
     * Return a {@link Set} of all domains appearing in all senders' email addresses
     *
     * @return A {@link Set} of domains
     * @throws ImapException
     */
    public Set<String> getSenderDomains() throws ImapException {
        return getDomains(getSenderMailAddresses());
    }

    /**
     * Return a {@link Set} of all senders' {@link MailAddress}es
     *
     * @return A {@link Set} of {@link MailAddress} objects, representing valid email addresses
     * @throws ImapException
     */
    public Set<MailAddress> getSenderMailAddresses() throws ImapException {
        try {
            List<Address> addresses = new ArrayList<Address>();

            List<Folder> availableFolders = getToplevelFolders();
            for (Folder folder : availableFolders) {
                try {
                    addresses.addAll(getSenderMailAddresses(folder));
                } catch (FolderNotFoundException e) {
                    // Somewhat buggy folder? No idea why this happens, ignore for now
                    //TODO
                }
            }

            return convertAdressesToMailAddresses(addresses);
        } catch (MessagingException e) {
            throw new ImapException("Error while trying to retrieve sender mail addresses", e);
        }
    }

    private List<Folder> getToplevelFolders() throws MessagingException {
        List<Folder> folders = new ArrayList<Folder>();
        folders.addAll(Arrays.asList(store.getPersonalNamespaces()));
        folders.addAll(Arrays.asList(store.getSharedNamespaces()));
        folders.add(store.getFolder("INBOX"));
        return folders;
    }

    private List<Address> getSenderMailAddresses(Folder folder) throws MessagingException {
        List<Address> fromAddresses = new ArrayList<Address>();
        folder.open(Folder.READ_ONLY);
        try {
            Message[] msgs = folder.getMessages();

            folder.fetch(msgs, fetchProfile); // bulk pre-fetch envelope attributes

            for (Message msg : msgs) {
                Address[] from = msg.getFrom();
                if (from != null) { // from can be null if no from in envelope
                    fromAddresses.addAll(Arrays.asList(from));
                }
            }

            for (Folder subFolder : folder.list()) {
                fromAddresses.addAll(getSenderMailAddresses(subFolder));
            }
        } catch (MessagingException e) {
            throw e;
        } finally {
            folder.close(false);
        }
        return fromAddresses;
    }
}
