package drinkteam.vue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import com.drinkteam.R;

import java.util.List;

import drinkteam.controleur.Utils;
import drinkteam.metier.Jeu;

/**
 * Created by Timothée on 18/06/2016.
 */
public class TrouverBar_Page extends Fragment implements OnMapReadyCallback {
	
	private MapView mMapView;
	private GoogleMap myMap;
	
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
		return inflater.inflate(R.layout.trouver_bar_page, container, false);
	}
	
	/**
	 * Ajout des éléments après la création de la vue
	 * @param view Vue créée
	 * @param savedInstanceState Ancien état de l'application si mise de côté
	 */
	@Override
	public void onViewCreated(@NonNull final View view, Bundle savedInstanceState){
		// titre de l'actionBar //
		Bundle args = getArguments();
		((MainActivity)getActivity()).setTitreActionBar(args.getString("titre_actionbar"));
		
		mMapView = view.findViewById(R.id.mapView);
		
		// ----- A vérifier ------- ///
		mMapView.onCreate(savedInstanceState);
		mMapView.onResume(); // needed to get the map to display immediately
		
		try {
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e) {
			Log.d("myapp", e.toString());
		}
	}
	
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		
		myMap = googleMap;
		
		// For showing a move to my location button
		// TODO Permission GOOGLE (décommenter pour voir l'erreur
		//myMap.setMyLocationEnabled(true);
		
		
		LatLng strasbourg = new LatLng(48.5, 7.7);
		
		// For dropping a marker at a point on the Map
		// Marche pas lol
		googleMap.addMarker(new MarkerOptions().position(strasbourg).title("Marker Title").snippet("Marker Description"));
		
		// For zooming automatically to the location of the marker
		// marche pas lol
		CameraPosition cameraPosition = new CameraPosition.Builder().target(strasbourg).zoom(12).build();
		myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		
		myMap.moveCamera(CameraUpdateFactory.newLatLng(strasbourg));
	}
}
