package es.ucm.deckdraw.ui.Adapter;

import android.app.NotificationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.ucm.deckdraw.R;
import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.data.dataBase.CurrentUserManager;
import es.ucm.deckdraw.data.dataBase.NotificationsAdmin;
import es.ucm.deckdraw.data.dataBase.UsersAdmin;
import es.ucm.deckdraw.ui.Fragment.FriendsFragment;
import es.ucm.deckdraw.util.Callback;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder>{

    private List<TUsers> friends;
    private FriendsFragment sch_frag;

    public FriendAdapter(FriendsFragment sch_frag) {
        this.friends = new ArrayList<>();
        this.sch_frag = sch_frag;
    }

    @NonNull
    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendAdapter.FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendViewHolder holder, int position) {
        TUsers friend = friends.get(position);
        holder.name.setText(friend.getUsername());
        CurrentUserManager currentUserManager = new CurrentUserManager(sch_frag.getContext());
        TUsers currentUser = currentUserManager.getCurrentUser();
        if(currentUser.getFriends().contains(friend.getIdusers())){
            holder.status.setText("Friend");
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
        }
        else if(friend.getFriendsRequest().contains(currentUser.getIdusers())){
            holder.status.setText("Request sent");
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
        }
        else{
            NotificationsAdmin nA = new NotificationsAdmin();
            holder.status.setText("Pending your response");
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);
            holder.acceptButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nA.acceptFriendRequest(currentUser.getIdusers(), friend.getIdusers(), new Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            sch_frag.showNotification("Friend request accepted");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            sch_frag.showNotification("Error accepting friend request");
                        }
                    });
                }
            });
            holder.declineButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nA.rejectFriendRequest(currentUser.getIdusers(), friend.getIdusers(), new Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            sch_frag.showNotification("Friend request declined");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            sch_frag.showNotification("Error declining friend request");
                        }
                    });
                }
            });
        }
    }

    public void setFriends(TUsers user) {
        UsersAdmin uA = new UsersAdmin();
        for (String friend : user.getFriends()) {
            uA.getUserByUid(friend, new Callback<TUsers>() {
                @Override
                public void onSuccess(TUsers data) {
                    friends.add(data);
                }
                @Override
                public void onFailure(Exception e) {

                }
            });
        }
        for (String friend : user.getFriendsRequest()) {
            uA.getUserByUid(friend, new Callback<TUsers>() {
                @Override
                public void onSuccess(TUsers data) {
                    friends.add(data);
                }
                @Override
                public void onFailure(Exception e) {

                }
            });
        }
        for (String friend : user.getFriendsSend()) {
            uA.getUserByUid(friend, new Callback<TUsers>() {
                @Override
                public void onSuccess(TUsers data) {
                    friends.add(data);
                }
                @Override
                public void onFailure(Exception e) {

                }
            });
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView status;
        Button acceptButton;
        Button declineButton;

        public FriendViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtFriendName);
            status = itemView.findViewById(R.id.txtFriendStatus);
            acceptButton = itemView.findViewById(R.id.btnAccept);
            declineButton = itemView.findViewById(R.id.btnDecline);
        }
    }
}
