package com.toysocialnetworkgui.domain.network;

import com.toysocialnetworkgui.domain.Friendship;
import com.toysocialnetworkgui.domain.User;
import com.toysocialnetworkgui.repository.FriendshipRepository;
import com.toysocialnetworkgui.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostFriendlyCommunity {
    private final UserRepository uRepo;
    private final FriendshipRepository fRepo;
    int nrUsersLongestPath;
    private final List<User> usersMostFrCom;
    private final List<Friendship> friendshipsMostFrCom;
    private final Map<String, Boolean> used;
    private final Map<String, UserNode> nodes;

    public MostFriendlyCommunity(UserRepository uRepo, FriendshipRepository fRepo, Map<String, Integer> com, int nrCommunities) {
        this.uRepo = uRepo;
        this.fRepo = fRepo;
        usersMostFrCom = new ArrayList<>();
        friendshipsMostFrCom = new ArrayList<>();
        used = new HashMap<>();
        nodes = new HashMap<>();
        for (User u : uRepo.getAll()) {
            nodes.put(u.getEmail(), new UserNode(u));
            used.put(u.getEmail(), false);
        }
        for (User u : uRepo.getAll()) {
            nodes.put(u.getEmail(), new UserNode(u));
            DFS(u.getEmail());
        }
    }

    /**
     * @return the users of the longest path from the friends network - List[User]
     */
    public List<User> getUsersMostFrCom() {
        return usersMostFrCom;
    }

    public int getNrUsers() {
        return nrUsersLongestPath;
    }

    /**
     * Finds the longest path in the community
     * @param e - the email of the user from whose node the dfs starts in the graph of the community
     */
    private void DFS(String e) {
        used.put(e, true);
        for (String em : fRepo.getUserFriends(uRepo.getUser(e).getEmail())) {
            if (used.get(em) == false) {
                nodes.put(em, new UserNode(uRepo.getUser(em), uRepo.getUser(e), nodes.get(e).steps + 1));
                DFS(em);
            }
        }
        if (nodes.get(e).steps + 1 > nrUsersLongestPath)
            updateAns(e);
        used.put(e, false);
    }

    private void updateAns(String e) {
        UserNode un = nodes.get(e);
        int nr = 0;
        usersMostFrCom.clear();
        friendshipsMostFrCom.clear();
        while (un.prevU != null) {
            nr++;
            usersMostFrCom.add(un.u);
            friendshipsMostFrCom.add(new Friendship(un.u, un.prevU));
            un = nodes.get(un.prevU.getEmail());
        }
        nr++;
        usersMostFrCom.add(un.u);
        nrUsersLongestPath = nr;
    }
}
