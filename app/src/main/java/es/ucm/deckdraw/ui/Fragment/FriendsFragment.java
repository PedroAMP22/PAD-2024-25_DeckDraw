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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import es.ucm.deckdraw.util.Callback;

public class FriendsFragment extends Fragment {

    private Context context;
    private Dialog dialog;

    private TUsers cUser;

    private String userUid;

    private NotificationsAdmin notiAdmin;
    private UsersAdmin usersAdmi;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context = requireContext();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_friend);
        dialog.setCancelable(true);

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        CurrentUserManager userManager = new CurrentUserManager(context);

        cUser = userManager.getCurrentUser();

        userUid = cUser.getIdusers();

        notiAdmin = new NotificationsAdmin();

        usersAdmi = new UsersAdmin();


        FloatingActionButton buttonAddFriend = view.findViewById(R.id.buttonAddFriend);

        buttonAddFriend.setOnClickListener((v) -> {
            showAddFriendDialog();
        });


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

            if(!uidFriend.isEmpty()){

                notiAdmin.newFriendReq(userUid, uidFriend, new Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        usersAdmi.getUserByUid(uidFriend, new Callback<TUsers>() {
                            @Override
                            public void onSuccess(TUsers data) {
                                notiAdmin.sendNotification(data.getNotifitacionToken(), userUid );
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                Log.e("ADD_NEW_FRIEND", e.toString());
                            }
                        });
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

        });

        dialog.show();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainScreenActivity) {
            MainScreenActivity mainScreenActivity = (MainScreenActivity) getActivity();
            mainScreenActivity.setToolbarTitle("Lista de amigos");
            mainScreenActivity.setHomeAsUpEnabled(false);
        }
    }
}
