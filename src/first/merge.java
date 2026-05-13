package first;

import java.util.ArrayList;
import java.util.List;

public class merge {
    public void merge(int[] nums1, int m, int[] nums2, int n){
        List list = new ArrayList();
        for (int i = 0; i < m; i++){
            list.add(nums1[i]);
        }
        for (int i = 0; i < n; i++){
            list.add(nums2[i]);
        }
        list.sort(null);
        for (int i = 0; i < m + n; i++){
            nums1[i] = (int) list.get(i);
        }
    }
}
