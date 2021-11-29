package com.toysocialnetworkgui.domain.network;

import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.FriendshipRepository;
import com.toysocialnetworkgui.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {
    private final UserRepository uRepo;
    private final FriendshipRepository fRepo;
    private int communitiesNr;
    private final Map<String, Integer> com;
    private MostFriendlyCommunity mfCom;

    public Network(UserRepository usRepo, FriendshipRepository frRepo) {
        this.uRepo = usRepo;
        this.fRepo = frRepo;
        this.com = new HashMap<>();
        reload();
    }

    /**
     * @return the users of the longest path from the friends network - List[User]
     */
    public List<User> getUsersMostFrCom() {
        reload();
        return mfCom.getUsersMostFrCom();
    }

    public void reload() {
        this.communitiesNr = countCommunities();
        this.mfCom = new MostFriendlyCommunity(uRepo, fRepo, com, communitiesNr);
    }

    /**
     * Returns a dictionary where the key is the number of the community and the value
     * is the list of the users' emails from that community
     * @return the dictionary with the users from the communities - Map[Integer, List[String]]
     */
    public Map<Integer, List<String>> getCommunities() {
        reload();
        Map<Integer, List<String>> comms = new HashMap<>();
        for (int i = 1; i <= communitiesNr; i++)
            comms.put(i, new ArrayList<String>());
        for (User u : uRepo.getAll()) {
            comms.get(com.get(u.getEmail())).add(u.getEmail());
        }
        return comms;
    }

    /**
     * Marks'true' all the friends of an user in used (a community)
     * @param e - the email of the user
     */
    private void dfs(String e, Integer c) {
        com.put(e, c);
        for (String em : fRepo.getUserFriends(uRepo.getUser(e).getEmail())) {
            if (com.get(em) == 0)
                dfs(em, c);
        }
    }

    public MostFriendlyCommunity getmfrCom() {
        return mfCom;
    }

    /**
     * Counts the communities in a network
     * @return no of communities - int
     */
    private int countCommunities() {
        List<User> users = uRepo.getAll();
        int nr = 0;
        com.clear();
        for (User u : users)
            com.put(u.getEmail(), 0);
        for (User u : users) {
            if (com.get(u.getEmail()) == 0) {
                nr++;
                dfs(u.getEmail(), nr);
            }
        }
        return nr;
    }

    /**
     * Returns the number of communities in a network
     * @return no communities - int
     */
    public int getNrCommunities() {
        return communitiesNr;
    }
}
