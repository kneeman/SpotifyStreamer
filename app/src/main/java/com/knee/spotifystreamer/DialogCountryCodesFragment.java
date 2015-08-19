package com.knee.spotifystreamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.knee.spotifystreamer.utils.Utils;

import java.util.Locale;


/**
 * Created by c_cknee on 7/27/2015.
 */
public class DialogCountryCodesFragment extends DialogFragment {
    private Context mContext;
    private SharedPreferences sharedPreferences;
    public DialogCountryCodesFragment(){
    }

    public static DialogCountryCodesFragment newInstance() {
        DialogCountryCodesFragment frag = new DialogCountryCodesFragment();
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        sharedPreferences = mContext.getSharedPreferences(ParentActivity.KEY_SHARED_PREFS, Activity.MODE_PRIVATE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] countryChosen = new String[1];
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final View transActionLayout = View.inflate(getActivity(), R.layout.dialog_country_code, null);
        alertDialogBuilder.setView(transActionLayout);
        final Spinner countrySpinner = (Spinner) transActionLayout.findViewById(R.id.spinner_country_codes);
        ArrayAdapter<String> countryAdapterArray = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, Utils.getSupportedCountryCodes());
        countrySpinner.setAdapter(countryAdapterArray);
        countrySpinner.setSelection(countryAdapterArray.getPosition(
                sharedPreferences.getString(ParentActivity.KEY_COUNTRY_MAP, Locale.getDefault().getCountry())));
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryChosen[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ParentActivity.KEY_COUNTRY_MAP, countryChosen[0]);
                editor.apply();
                Toast.makeText(getActivity(), getString(R.string.confirm_country_code_changed) + " "
                        + countryChosen[0], Toast.LENGTH_LONG).show();
                DialogCountryCodesFragment.this.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogCountryCodesFragment.this.dismiss();
            }
        });

        return alertDialogBuilder.create();

    }
}
