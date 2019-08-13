package com.nurif.skripsi.lita.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nurif.skripsi.lita.MainActivity;
import com.nurif.skripsi.lita.PowerAdapter;
import com.nurif.skripsi.lita.R;
import com.nurif.skripsi.lita.ReportActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;

public class ListEnergyFragment extends Fragment implements PowerAdapter.onItemSelected {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private MqttAndroidClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_energy, container, false);
        setHasOptionsMenu(true);

        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.rv_content);

        client = ((MainActivity) getActivity()).getClient();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setToolbar();

        setListView();
    }

    private void setToolbar() {
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_settings_white_24dp));
    }

    private void setListView() {
        ArrayList<String> power = new ArrayList<>();
        power.add("Lampu Taman");
        power.add("Ruang Keluarga");
        power.add("Kamar");
        power.add("Kebun Belakang");

        PowerAdapter powerAdapter = new PowerAdapter(power, this);
        recyclerView.setAdapter(powerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_content, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_settings:
                setDisconnectAlertDialog();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDisconnectAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage("Apakah kamu yakin untuk memutuskan hubungan?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (client.isConnected()) {
                            try {
                                client.disconnect();
                                getFragmentManager().popBackStack();
                            } catch (MqttException e) {

                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void setEnergy(int position) {
        if (position != 0) {
            Toast.makeText(getActivity(), "Perangkat belum terpasang", Toast.LENGTH_SHORT).show();
        } else {
            ((MainActivity) getActivity()).setContent(new EnergyFragment());
        }
    }

    @Override
    public void viewReport() {
        getActivity().startActivity(new Intent(getActivity(), ReportActivity.class));
    }
}
