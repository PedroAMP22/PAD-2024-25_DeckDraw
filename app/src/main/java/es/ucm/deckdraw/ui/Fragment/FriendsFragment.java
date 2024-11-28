package es.ucm.deckdraw.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.ucm.deckdraw.data.Objects.users.TUsers;
import es.ucm.deckdraw.ui.Activities.MainScreenActivity;
import es.ucm.deckdraw.R;

public class FriendsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        ShowFriendFragment sf = new ShowFriendFragment(new TUsers());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sf).addToBackStack(null).commit();
        return inflater.inflate(R.layout.fragment_friends, container, false);
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
