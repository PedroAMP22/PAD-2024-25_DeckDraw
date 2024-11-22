package es.ucm.deckdraw.ui.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;

public class FriendsFragment extends Fragment {

    private Context context;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context = requireContext();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_friend);
        dialog.setCancelable(true);

        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    private void showDialog(){
        EditText editTextAmigod;
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
