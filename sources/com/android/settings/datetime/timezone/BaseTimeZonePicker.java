package com.android.settings.datetime.timezone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1988R$menu;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.datetime.timezone.BaseTimeZoneAdapter;
import com.android.settings.datetime.timezone.model.TimeZoneData;
import com.android.settings.datetime.timezone.model.TimeZoneDataLoader;
import com.google.android.material.appbar.AppBarLayout;
import java.util.Locale;

public abstract class BaseTimeZonePicker extends InstrumentedFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private BaseTimeZoneAdapter mAdapter;
    protected AppBarLayout mAppBarLayout;
    private final boolean mDefaultExpandSearch;
    private RecyclerView mRecyclerView;
    private final boolean mSearchEnabled;
    private final int mSearchHintResId;
    private SearchView mSearchView;
    private TimeZoneData mTimeZoneData;
    private final int mTitleResId;

    public interface OnListItemClickListener<T extends BaseTimeZoneAdapter.AdapterItem> {
        void onListItemClick(T t);
    }

    /* access modifiers changed from: protected */
    public abstract BaseTimeZoneAdapter createAdapter(TimeZoneData timeZoneData);

    public boolean onQueryTextSubmit(String str) {
        return false;
    }

    protected BaseTimeZonePicker(int i, int i2, boolean z, boolean z2) {
        this.mTitleResId = i;
        this.mSearchHintResId = i2;
        this.mSearchEnabled = z;
        this.mDefaultExpandSearch = z2;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        getActivity().setTitle(this.mTitleResId);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C1987R$layout.recycler_view, viewGroup, false);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(C1985R$id.recycler_view);
        this.mRecyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mAppBarLayout = (AppBarLayout) getActivity().findViewById(C1985R$id.app_bar);
        disableToolBarScrollableBehavior();
        getLoaderManager().initLoader(0, (Bundle) null, new TimeZoneDataLoader.LoaderCreator(getContext(), new BaseTimeZonePicker$$ExternalSyntheticLambda0(this)));
        return inflate;
    }

    public void onTimeZoneDataReady(TimeZoneData timeZoneData) {
        if (this.mTimeZoneData == null && timeZoneData != null) {
            this.mTimeZoneData = timeZoneData;
            BaseTimeZoneAdapter createAdapter = createAdapter(timeZoneData);
            this.mAdapter = createAdapter;
            RecyclerView recyclerView = this.mRecyclerView;
            if (recyclerView != null) {
                recyclerView.setAdapter(createAdapter);
            }
        }
    }

    /* access modifiers changed from: protected */
    public Locale getLocale() {
        return getContext().getResources().getConfiguration().getLocales().get(0);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (this.mSearchEnabled) {
            menuInflater.inflate(C1988R$menu.time_zone_base_search_menu, menu);
            MenuItem findItem = menu.findItem(C1985R$id.time_zone_search_menu);
            findItem.setOnActionExpandListener(this);
            SearchView searchView = (SearchView) findItem.getActionView();
            this.mSearchView = searchView;
            searchView.setQueryHint(getText(this.mSearchHintResId));
            this.mSearchView.setOnQueryTextListener(this);
            if (this.mDefaultExpandSearch) {
                findItem.expandActionView();
                this.mSearchView.setIconified(false);
                this.mSearchView.setActivated(true);
                this.mSearchView.setQuery("", true);
            }
            TextView textView = (TextView) this.mSearchView.findViewById(16909412);
            textView.setPadding(0, textView.getPaddingTop(), 0, textView.getPaddingBottom());
            View findViewById = this.mSearchView.findViewById(16909408);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
            layoutParams.setMarginStart(0);
            layoutParams.setMarginEnd(0);
            findViewById.setLayoutParams(layoutParams);
        }
    }

    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        this.mAppBarLayout.setExpanded(false, false);
        ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, false);
        return true;
    }

    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        this.mAppBarLayout.setExpanded(false, false);
        ViewCompat.setNestedScrollingEnabled(this.mRecyclerView, true);
        return true;
    }

    public boolean onQueryTextChange(String str) {
        BaseTimeZoneAdapter baseTimeZoneAdapter = this.mAdapter;
        if (baseTimeZoneAdapter == null) {
            return false;
        }
        baseTimeZoneAdapter.getFilter().filter(str);
        return false;
    }

    private void disableToolBarScrollableBehavior() {
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });
        ((CoordinatorLayout.LayoutParams) this.mAppBarLayout.getLayoutParams()).setBehavior(behavior);
    }
}
