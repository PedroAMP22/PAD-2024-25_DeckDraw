package es.ucm.deckdraw.ui.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import es.ucm.deckdraw.data.Objects.decks.TDecks;
import es.ucm.deckdraw.data.Objects.users.TUsers;

import es.ucm.deckdraw.data.dataBase.CurrentUserManager;
import es.ucm.deckdraw.data.dataBase.UsersAdmin;

import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.TextView;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Text;

import es.ucm.deckdraw.data.dataBase.NotificationsAdmin;
import es.ucm.deckdraw.ui.Adapter.FriendAdapter;
import es.ucm.deckdraw.ui.ViewModel.SharedViewModel;
import es.ucm.deckdraw.util.Callback;

public class FriendsFragment extends Fragment {

    private Context context;
    private Dialog dialog;

    private TUsers cUser;

    private String userUid;

    private CurrentUserManager userManager;

    private NotificationsAdmin notiAdmin;
    private UsersAdmin usersAdmi;

    private RecyclerView recyclerView;
    private FriendAdapter friendsAdapter;
    private SharedViewModel sharedViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        context = requireContext();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_friend);
        dialog.setCancelable(true);

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        userManager = new CurrentUserManager(context);

        cUser = userManager.getCurrentUser();

        userUid = cUser.getIdusers();

        notiAdmin = new NotificationsAdmin();

        usersAdmi = new UsersAdmin();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        FloatingActionButton buttonAddFriend = view.findViewById(R.id.buttonAddFriend);

        buttonAddFriend.setOnClickListener((v) -> {
            showAddFriendDialog();
        });

        recyclerView = view.findViewById(R.id.recyclerViewFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        friendsAdapter = new FriendAdapter(this);
        recyclerView.setAdapter(friendsAdapter);
        friendsAdapter.setFriends(cUser);
        return view;
    }

    private void showAddFriendDialog(){


        context = requireContext();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_friend);
        dialog.setCancelable(true);

        TextView friendIDTV = dialog.findViewById(R.id.friendID);

        friendIDTV.setText(userUid);

        ImageButton copyMyUidIV = dialog.findViewById(R.id.copyIDButton);


        copyMyUidIV.setOnClickListener((v) -> {
            //Copy to clipboard

            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("UserUid", userUid);
            clipboard.setPrimaryClip(clip);
        });

        Button addFriendButton = dialog.findViewById(R.id.addFriendButton);

        addFriendButton.setOnClickListener((v) ->{

            EditText editTextUidFriend = dialog.findViewById(R.id.editAddFriendID);

            String uidFriend = editTextUidFriend.getText().toString();

            if(!cUser.getFriends().contains(uidFriend) && !cUser.getFriendsSend().contains(uidFriend) && !cUser.getFriendsRequest().contains(uidFriend)){
                if(!uidFriend.isEmpty()){

                    notiAdmin.newFriendReq(userUid, uidFriend, new Callback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {

                            cUser.addSend(uidFriend);
                            userManager.saveUserSession(cUser);

                            showNotification("Friend Request Sent");
                            friendsAdapter.setFriends(cUser);

                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            Log.e("ADD_NEW_FRIEND", e.toString());
                        }
                    });
                }
                else{
                    Toast.makeText(context, "ID required", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(context, "Friendship already requested", Toast.LENGTH_SHORT).show();

            }

            dialog.dismiss();
        });

        dialog.show();



    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            mainScreenActivity.setToolbarTitle(getString(R.string.friend_list));
            mainScreenActivity.setHomeAsUpEnabled(false);
        }
    }

    public void showNotification(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    public void onShowUser(TUsers user) {
        ShowFriendFragment showFriend = new ShowFriendFragment();
        sharedViewModel.setCurrentFriend(user);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, showFriend)
                .addToBackStack(null)
                .commit();
    }
}
