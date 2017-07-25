package com.nakhmedov.gmuzbprice;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nakhmedov.gmuzbprice.constants.ContextConstants;
import com.nakhmedov.gmuzbprice.entity.CarModel;
import com.nakhmedov.gmuzbprice.entity.Currency;
import com.nakhmedov.gmuzbprice.entity.CurrencyInfo;
import com.nakhmedov.gmuzbprice.net.GmHttpService;
import com.nakhmedov.gmuzbprice.viewmodel.CarListViewModel;
import com.nakhmedov.gmuzbprice.viewmodel.CurrencyViewModel;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static okhttp3.OkHttpClient.Builder;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SETTINGS_REQUEST_CODE = 101;

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.cars_recycler_view) RecyclerView mCarRecyclerView;
    @BindView(R.id.price_view) TextView mPriceView;
    @BindView(R.id.position_text) TextView mPositionTV;
    @BindView(R.id.bottom_sheet) View bottomSheet;
    @BindView(R.id.positionsRecyclerView) RecyclerView listRecyclerView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.adView) AdView mAdView;


    private DatabaseReference mFirebaseDatabaseReference;
    //    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private CarAdapter carAdapter;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private RecyclerItemAdapter carPositionsAdapter;
    private CarListViewModel viewModel;
    private CurrencyViewModel currencyViewModel;
    private CurrencyInfo mCurrencyInfo;
    private String lastDate = new Date().toString();

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setSupportActionBar(toolbar);
        mToolbar.setTitle(getString(R.string.app_name));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        RecyclerView.LayoutManager mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mCarRecyclerView.setLayoutManager(mGridLayoutManager);

        carAdapter = new CarAdapter(this, listener);
        mCarRecyclerView.setAdapter(carAdapter);

        //AdView

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("9955D816375FF5AF7DDE1FAA0B2B0413")
                .build();
        mAdView.loadAd(adRequest);


        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mBottomSheetBehavior.setPeekHeight(200);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        listRecyclerView.setHasFixedSize(true);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        carPositionsAdapter = new RecyclerItemAdapter(this, listener);
        listRecyclerView.setAdapter(carPositionsAdapter);

        sendRequestCurrencyApi();

//        List<Car> carList = appDatabase.carDao().getCarList();
        viewModel = ViewModelProviders.of(this).get(CarListViewModel.class);
        currencyViewModel = ViewModelProviders.of(this).get(CurrencyViewModel.class);
        viewModel.getCarList().observe(MainActivity.this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> carModels) {
                System.out.println("local  Parsed = " + carModels.size());
                carAdapter.swapData(carModels);
                if (carModels.size() > 0) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

         currencyViewModel.getInfo().observe(MainActivity.this, new Observer<CurrencyInfo>() {
            @Override
            public void onChanged(@Nullable CurrencyInfo currencyInfo) {
                if (currencyInfo != null) {
                    mCurrencyInfo = currencyInfo;
                    lastDate = mCurrencyInfo.getDate();
                    mToolbar.setSubtitle(getString(R.string.last_update, lastDate));
                }
            }
        });

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mCarReference = mFirebaseDatabaseReference.child(ContextConstants.CAR_CHILD);
        mCarReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, CarModel> map = (Map<String, CarModel>) dataSnapshot.getValue();
                final List<CarModel> list = new ArrayList(map.values().size());
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    it.remove(); // avoids a ConcurrentModificationException
                    String carType = pair.getKey().toString();
                    ArrayList<HashMap<String, String>> carPositionsList = (ArrayList<HashMap<String, String>>) pair.getValue();
                    for (int i = 1; i < carPositionsList.size(); i++) {
                        HashMap<String, String> carMap = carPositionsList.get(i);
                        final CarModel carModel = new CarModel(carMap.get("name"), carMap.get("price"), carType, "");
                        list.add(carModel);
                    }
                }
                viewModel.insertItems(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_main).setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_REQUEST_CODE) {
            updateLanguageUI();
        }
    }

    private void updateLanguageUI() {
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setSubtitle(getString(R.string.last_update, lastDate));
        mPositionTV.setText(getString(R.string.car_positions));
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_main).setTitle(getString(R.string.main));
        menu.findItem(R.id.nav_settings).setTitle(getString(R.string.action_settings));
        menu.findItem(R.id.nav_share).setTitle(getString(R.string.share));
        menu.findItem(R.id.comunicate).setTitle(getString(R.string.communicate));
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_title))
                .setText(getString(R.string.app_name));
    }

    private void sendRequestCurrencyApi() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new Builder()
//                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(
                        new Persister(new AnnotationStrategy()))
                )
                .baseUrl(ContextConstants.CURRENCY_URL)
                .build();

        GmHttpService service = retrofit.create(GmHttpService.class);
        service.listCurrency().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Serializer serializer = new Persister();
                        String responseText = response.body().string();
                        Currency currency = serializer.read(Currency.class, responseText);
                        List<CurrencyInfo> list = currency.getProperties();
                        int size = list.size()-1;
                        for (int i = size; i > 0; --i) {
                            CurrencyInfo info = list.get(i);
                            boolean isDollar = info.getName().equals("USD");
                            if (isDollar) {
                                currencyViewModel.insertItem(info);
                                break;
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    ClickListener listener = new ClickListener() {
        @Override
        public void onCarItemClick(View view) {
            String carName = (String) view.getTag();
            viewModel.getCarPositions(carName).observe(MainActivity.this, new Observer<List<CarModel>>() {
                @Override
                public void onChanged(@Nullable List<CarModel> carModels) {
//                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    if (carModels != null && carModels.size() > 0) {
                        showPrice(carModels.get(0));
                        carPositionsAdapter.addItems(carModels);
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
//                    }
                }
            });
        }

        @Override
        public void onCarPositionClick(View view) {
            CarModel carModel = (CarModel) view.getTag();
            showPrice(carModel);
        }
    };

    private void showPrice(CarModel carModel) {
        String priceTxt = carModel.getPrice();
        if (priceTxt == null)
            return;

        double price = Integer.parseInt(priceTxt);
        double currencyRate = 0;
        if (mCurrencyInfo != null) {
            currencyRate = Double.parseDouble(mCurrencyInfo.getRate());
        }
        if (currencyRate > 0) {
            price = price * currencyRate;
        }
        DecimalFormat df = new DecimalFormat("#,###");
        String result = df.format(price);

        mPriceView.setText(result);
    }

    public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
        private final Context mContext;
        private final ClickListener mListener;
        private List<String> carList = new ArrayList<>(20);

        public CarAdapter(Context context, ClickListener listener) {
            this.mContext = context;
            this.mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_car, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            String carName = carList.get(holder.getAdapterPosition());
            holder.mCarRootView.setTag(carName);
            holder.mCarRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCarItemClick(holder.mCarRootView);
                }
            });
            holder.mCarTextView.setText(carName);
        }

        @Override
        public int getItemCount() {
            return carList.size();
        }

        public void swapData(List<String> list) {
            this.carList = list;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.car_name)
            TextView mCarTextView;
            @BindView(R.id.car_item_view)
            RelativeLayout mCarRootView;

            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }
    }

    public interface ClickListener {
        void onCarItemClick(View v);
        void onCarPositionClick(View view);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {

        } else if (id == R.id.nav_settings) {
            startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), SETTINGS_REQUEST_CODE);

        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_txt));
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class RecyclerItemAdapter extends RecyclerView.Adapter<RecyclerItemAdapter.ViewHolder> {
        private final Context mContext;
        private List<CarModel> carList = new ArrayList<>(10);
        private ClickListener mListener;
        private int selectedPosition;

        public RecyclerItemAdapter(Context context, ClickListener listener) {
            this.mContext = context;
            this.mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerItemAdapter.ViewHolder(
                    LayoutInflater.from(mContext).inflate(R.layout.item_car_position, parent, false));

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            CarModel carModel = carList.get(holder.getAdapterPosition());
            holder.mCarTextView.setTag(carModel);
            holder.mCarItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCarPositionClick(holder.mCarTextView);
                    selectedPosition = position;
                    notifyDataSetChanged();

                }
            });
            holder.mCarTextView.setText(carModel.getName());
            holder.mCarPriceView.setText("$" + carModel.getPrice());
            if(selectedPosition == position) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.light_gray));
            } else{
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }


        }

        @Override
        public int getItemCount() {
            return carList.size();
        }

        public void addItems(List<CarModel> carModels) {
            selectedPosition = 0;
            this.carList = carModels;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.car_item_view) RelativeLayout mCarItemView;
            @BindView(R.id.car_name) TextView mCarTextView;
            @BindView(R.id.car_price) TextView mCarPriceView;

            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }

        }

    }

    //        mFirebaseAdapter = new FirebaseRecyclerAdapter<CarModel, CarViewHolder>(
//                CarModel.class,
//                R.layout.item_car,
//                CarViewHolder.class,
//                mFirebaseDatabaseReference.child(ContextConstants.CAR_CHILD)) {
//
//            @Override
//            protected void populateViewHolder(CarViewHolder viewHolder, CarModel carModel, int position) {
//                if ( carModel.getName() != null) {
//                    viewHolder.mCarTextView.setText(carModel.getName());
//                }
//            }
//
//            @Override
//            public CarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                CarViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
//                viewHolder.setOnClickListener(new CarViewHolder.ClickListener() {
//                    @Override
//                    public void onCarItemClick(int position) {
//                        Toast.makeText(MainActivity.this, "Item clicked at " + position, Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return viewHolder;
//            }
//        };

    //    static class CarViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.car_name) TextView mCarTextView;
//        @BindView(R.id.car_item_view) RelativeLayout mCarRootView;
//
//        private ClickListener mListener;
//
//        public interface ClickListener {
//            void onCarItemClick(int position);
//        }
//
//        public CarViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//
//            mCarRootView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onCarItemClick(getAdapterPosition());
//                }
//            });
//        }
//
//        public void setOnClickListener(CarViewHolder.ClickListener clickListener){
//            mListener = clickListener;
//        }
//
//    }

}
