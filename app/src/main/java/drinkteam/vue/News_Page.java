package drinkteam.vue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drinkteam.R;

/**
 * Created by Timothée on 18/06/2016.
 *
 * Page (Fragment) "News" accessible depuis le menu
 */
public class News_Page extends Fragment {
	/**
	 * Création de la vue à partir du layout dédié
	 * @param inflater Objet utilisé pour remplir la vue
	 * @param container Vue parente qui contient la vue
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 * @return Vue créée
	 */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// associe le layout au fragment //
        return inflater.inflate(R.layout.news_page, container, false);
    }
	
	/**
	 * Ajout des éléments après la création de la vue
	 * @param view Vue créée
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 */
	@Override
	public void onViewCreated(@NonNull final View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		
		// titre de l'actionBar //
		Bundle args = getArguments();
		((MainActivity)getActivity()).setTitreActionBar(args.getString("titre_actionbar"));
		
	}
}
