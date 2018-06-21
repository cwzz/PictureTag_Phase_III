package com.enums;

import com.enums.NumberOfDays;
import com.enums.PointsRange;
import com.enums.ProjectType;
import com.enums.SearchProState;
import lombok.Data;

@Data
public class SearchReq {

        private String keywords;
        private ProjectType type;
        private SearchProState state;
        private PointsRange pointsRange;
        private NumberOfDays numberOfDays;

        public SearchReq(){}
}
