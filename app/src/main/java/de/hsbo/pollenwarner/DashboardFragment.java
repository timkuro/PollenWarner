package de.hsbo.pollenwarner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.hsbo.pollenwarner.location.Point;
import de.hsbo.pollenwarner.location.Polygon;
import de.hsbo.pollenwarner.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Point pt = new Point(10,2);
        List<Point> list = new ArrayList<Point>();
        list.add(new Point(0,0));
        list.add(new Point(5,0));
        list.add(new Point(5,5));
        list.add(new Point(0,5));
        Polygon polygon = new Polygon(list);
        polygon.PointInPolygon(pt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }
}