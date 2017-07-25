package com.nakhmedov.gmuzbprice.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nakhmedov.gmuzbprice.R;
import com.nakhmedov.gmuzbprice.SettingsActivity;
import com.nakhmedov.gmuzbprice.constants.PrefLab;

import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/6/17
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String TAG = SettingsFragment.class.getCanonicalName();

    private static final int REQUEST_INVITE = 101;
    private SharedPreferences prefs;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private ListPreference languageOptionPreference;
    private Preference sharePreference;
    private Preference ratePreference;
    private Preference invitePreference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preference);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        languageOptionPreference = (ListPreference) findPreference(PrefLab.CHOOSE_LANGUAGE);
        setLanguageSummary(prefs.getString(PrefLab.CHOOSE_LANGUAGE, getString(R.string.russian)));
        sharePreference = findPreference(PrefLab.SHARE);
        ratePreference = findPreference(PrefLab.RATE);
        invitePreference = findPreference(PrefLab.INVITE);

        sharePreference.setOnPreferenceClickListener(this);
        ratePreference.setOnPreferenceClickListener(this);
        invitePreference.setOnPreferenceClickListener(this);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case PrefLab.CHOOSE_LANGUAGE: {
                        String value = sharedPreferences.getString(key, getString(R.string.russian));
                        Locale locale = new Locale(value);
                        Locale.setDefault(locale);
                        Configuration config = getActivity().getBaseContext().getResources().getConfiguration();
                        config.locale = locale;
                        getActivity().getBaseContext().getResources().updateConfiguration(config,
                                getActivity().getBaseContext().getResources().getDisplayMetrics());
                        setLanguageSummary(value);
                        updateUI();
                        break;
                    }
                    case PrefLab.NTFY_NEW_VERSION: {

                        break;
                    }
                }
            }
        };
    }

    private void updateUI() {
        ((SettingsActivity) getActivity()).getToolbar().setTitle(getString(R.string.action_settings));
        languageOptionPreference.setTitle(getString(R.string.choose_language));
        sharePreference.setTitle(getString(R.string.share));
        ratePreference.setTitle(getString(R.string.rate));
        invitePreference.setTitle(getString(R.string.invite));
    }

    private void setLanguageSummary(String value) {
        if (value.equals("ru")) {
            value = getString(R.string.russian);
        } else if (value.equals("en")) {
            value = getString(R.string.english);
        } else {
            value = getString(R.string.uzbek);
        }
        languageOptionPreference.setSummary(value);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String itemKey = preference.getKey();
        switch (itemKey) {
            case PrefLab.SHARE: {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_txt));
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                break;
            }
            case PrefLab.RATE: {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
                break;
            }
            case PrefLab.INVITE: {
                sendInvitation();
                break;
            }
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
}
