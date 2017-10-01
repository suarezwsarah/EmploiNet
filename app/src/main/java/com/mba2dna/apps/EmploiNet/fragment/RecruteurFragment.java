package com.mba2dna.apps.EmploiNet.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.activities.ActivityMain;

import com.mba2dna.apps.EmploiNet.adapter.RecruteurAdapter;
import com.mba2dna.apps.EmploiNet.data.SQLiteHandler;
import com.mba2dna.apps.EmploiNet.data.SharedPref;
import com.mba2dna.apps.EmploiNet.loader.ApiClientLoader;
import com.mba2dna.apps.EmploiNet.model.ApiClient;

import com.mba2dna.apps.EmploiNet.model.Recruteur;
import com.mba2dna.apps.EmploiNet.utils.Callback;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecruteurFragment extends Fragment {


    public static String TAG_CATEGORY = "com.mba2dna.apps.EmploiNet.tagCategory";
    private static int category_id;

    private View view;
    private static RecyclerView recyclerView;
    private View lyt_progress;
    private static View lyt_not_found;
    private static RecruteurAdapter adapter;
    private SQLiteHandler db;
    Fragment fragment = null;
    Bundle bundle = null;
    TextView mTitle;

    private List<Recruteur> recruteur = new ArrayList<>();

    private SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recruteur, null);

        // activate fragment menu
        setHasOptionsMenu(true);

        db = new SQLiteHandler(getActivity());
        sharedPref = new SharedPref(getActivity());


        lyt_progress = view.findViewById(R.id.lyt_progress);
        lyt_not_found = view.findViewById(R.id.lyt_not_found);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), Tools.getGridSpanCount(getActivity()));

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? Tools.getGridSpanCount(getActivity()) : 1;
            }
        });

        recyclerView.setLayoutManager(layoutManager);

        // getDB();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView v, int state) {
                super.onScrollStateChanged(v, state);
                if (state == v.SCROLL_STATE_DRAGGING || state == v.SCROLL_STATE_SETTLING) {
                    ActivityMain.animateFab(true);
                } else {
                    ActivityMain.animateFab(false);
                }
            }
        });

        actionRefresh();
        return view;
    }


    private static void checkItems() {

        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_not_found.setVisibility(View.GONE);
        }

    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            actionRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionRefresh() {
        boolean conn = Tools.cekConnection(getActivity().getApplicationContext(), view);
        if (conn) {
            if (!onProcess) {
                onRefresh();
            } else {
                // Snackbar.make(view, "Task still running", Snackbar.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar.make(view, "التحديث مازال قائما", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getActivity(), tv);
                snackbar.show();
            }
        } else {
          //  getDB();
            // Tools.noConnectionSnackBar(view);
        }
    }

   /* private void getDB() {
        recruteur = db.getAllCategory();
        adapter = new CategoriesAdapter(getContext(), recruteur);

        adapter.setOnItemClickListener(new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, Category c) {
                Snackbar snackbar = Snackbar.make(view, c.category, Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getActivity(), tv);
                snackbar.show();
               /* Intent intent = new Intent(getContext(), ActivityMain.class);
                intent.putExtra(ActivityMain.TAG_NAME, c.type_activite);
                startActivity(intent);*/
              /*  bundle = new Bundle();
                fragment = new OffresFragment();
                bundle.putInt(OffresFragment.TAG_ID, c.cat_id);
                bundle.putString(OffresFragment.TAG_NAME, c.category);
                fragment.setArguments(bundle);

                ActivityMain.mTitle.setText(c.category);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, fragment);
                fragmentTransaction.commit();
            }
        });
        recyclerView.setAdapter(adapter);
        checkItems();
    }*/

    private boolean onProcess = false;

    private void onRefresh() {
       /* Snackbar snackbar = Snackbar.make(view, "تحديث البيانات...", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        CommonUtils.setRobotoThinFont(getActivity(), tv);
        snackbar.show();*/
        onProcess = true;
        showProgress(onProcess);
        ApiClientLoader task = new ApiClientLoader(new Callback<ApiClient>() {
            @Override
            public void onSuccess(ApiClient result) {
                final List<Recruteur> finalListRecruteur = result.recruteur;
                // db.addListCategory(result.reciepes_category);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //TODO your background code
                        // db.addListCategory(finalListCategory);
                     //   db.addListCategory(finalListCategory);

                        // db.addListImages(result.images);
                    }


                });


                // recruteur = db.getAllCategory();
                adapter = new RecruteurAdapter(getContext(), result.recruteur);
                adapter.setOnItemClickListener(new RecruteurAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, Recruteur c) {
                        Snackbar snackbar = Snackbar.make(view, c.recruteur, Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                        TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                        CommonUtils.setRobotoThinFont(getActivity(), tv);
                        snackbar.show();
                       /* Intent intent = new Intent(getContext(), ActivityMain.class);
                        intent.putExtra(ActivityMain.TAG_NAME, c.type_activite);
                        startActivity(intent);*/
                        bundle = new Bundle();
                        fragment = new OffresFragment();
                        bundle.putString(OffresFragment.TAG_TYPE, "RECRUTEUR");
                        bundle.putInt(OffresFragment.TAG_ID, c.rec_id);
                        bundle.putString(OffresFragment.TAG_NAME, c.recruteur);
                        fragment.setArguments(bundle);

                        ActivityMain.mTitle.setText(c.recruteur);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_content, fragment);
                        fragmentTransaction.commit();
                    }
                });
                recyclerView.setAdapter(adapter);
                checkItems();
                sharedPref.setRefreshReciepes(false);
                Snackbar snackbar = Snackbar.make(view, "Mise à jour réussie", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getActivity(), tv);
                snackbar.show();
                onProcess = false;
                showProgress(onProcess);
            }

            @Override
            public void onError(String result) {
                onProcess = false;
                showProgress(onProcess);
                // Snackbar.make(view, "Une erreur s’est produite lors de l’envoi de commandes au serveur", Snackbar.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(view, "Une erreur s’est produite lors de l’envoi de commandes au serveur", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                CommonUtils.setRobotoThinFont(getActivity(), tv);
                snackbar.show();
            }
        });
        task.execute("?toprecruter=true");
    }

    private void showProgress(boolean show) {
        if (show) {
            lyt_progress.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            lyt_progress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


}

