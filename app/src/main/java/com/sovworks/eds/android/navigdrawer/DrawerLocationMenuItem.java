package com.sovworks.eds.android.navigdrawer;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.sovworks.eds.android.R;
import com.sovworks.eds.android.filemanager.activities.FileManagerActivity;
import com.sovworks.eds.android.locations.activities.LocationSettingsActivity;
import com.sovworks.eds.android.locations.closer.fragments.LocationCloserBaseFragment;
import com.sovworks.eds.android.locations.opener.fragments.LocationOpenerBaseFragment;
import com.sovworks.eds.locations.Location;
import com.sovworks.eds.locations.LocationsManager;

public class DrawerLocationMenuItem extends DrawerMenuItemBase
{
    public static class Opener extends LocationOpenerBaseFragment
    {
        @Override
        public void onLocationOpened(Location location)
        {
            ((FileManagerActivity)getActivity()).goTo(location);
        }
    }

    public DrawerLocationMenuItem(Location location, DrawerControllerBase drawerController)
    {
        super(drawerController);
        _location = location;
    }

    public Location getLocation()
    {
        return _location;
    }

    @Override
    public String getTitle()
    {
        return _location.getTitle();
    }

    @Override
    public int getViewType()
    {
        return 2;
    }

    @Override
    public void updateView(View view, @SuppressWarnings("UnusedParameters") int position)
    {
        super.updateView(view, position);
        ImageView iv = (ImageView) view.findViewById(R.id.close);
        if(iv!=null)
        {
            if(LocationsManager.isOpenableOpen(_location))
            {
                iv.setVisibility(View.VISIBLE);
                iv.setOnClickListener(_closeIconClickListener);
            }
            else
                iv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view, int position)
    {
        openLocation();
        super.onClick(view, position);
    }

    @Override
    public boolean onLongClick(View view, int position)
    {
        if(hasSettings())
        {
            openLocationSettings();
            return true;
        }
        return false;
    }

    public void openLocation()
    {
        FragmentManager fm = getDrawerController().getMainActivity().getFragmentManager();
        String openerTag = LocationOpenerBaseFragment.getOpenerTag(_location);
        if(fm.findFragmentByTag(openerTag)==null)
        {
            LocationOpenerBaseFragment opener = getOpener();
            opener.setArguments(getOpenerArgs());
            fm.beginTransaction().add(opener, openerTag).commit();
        }
    }

    public void closeLocation()
    {
        FragmentManager fm = getDrawerController().getMainActivity().getFragmentManager();
        String closerTag = LocationCloserBaseFragment.getCloserTag( _location);
        if(fm.findFragmentByTag(closerTag)==null)
        {
            LocationCloserBaseFragment closer = getCloser();
            closer.setArguments(getCloserArgs());
            fm.beginTransaction().add(closer, closerTag).commit();
        }
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.drawer_location_item;
    }

    protected LocationCloserBaseFragment getCloser()
    {
        return LocationCloserBaseFragment.getDefaultCloserForLocation(_location);
    }

    protected LocationOpenerBaseFragment getOpener()
    {
        return new Opener();
    }

    protected Bundle getOpenerArgs()
    {
        Bundle b = new Bundle();
        LocationsManager.storePathsInBundle(b,_location, null);
        return b;
    }

    protected Bundle getCloserArgs()
    {
        Bundle b = new Bundle();
        LocationsManager.storePathsInBundle(b,_location, null);
        return b;
    }

    protected void openLocationSettings()
    {
        Intent i = new Intent(getContext(), LocationSettingsActivity.class);
        LocationsManager.storePathsInIntent(i, _location, null);
        getContext().startActivity(i);
    }

    protected boolean hasSettings()
    {
        return false;
    }

    private final Location _location;
    private final View.OnClickListener _closeIconClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            closeLocation();
        }
    };
}