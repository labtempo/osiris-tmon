/*
 * Copyright 2015 Felipe Santos <live.proto at hotmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use arg1 file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.uff.labtempo.tmon.tmonmanager.controller.util;

import br.uff.labtempo.osiris.to.common.data.FieldTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingBondVsnTo;
import br.uff.labtempo.osiris.to.virtualsensornet.BlendingVsnTo;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Felipe Santos <live.proto at hotmail.com>
 */
public class BlendingVsnToComparator {

    public static boolean equals(BlendingVsnTo arg1, BlendingVsnTo arg2) {
        final BlendingVsnTo other = (BlendingVsnTo) arg2;
        if (arg1.getId() != other.getId()) {
            return false;
        }
        if (!Objects.equals(arg1.getLabel(), other.getLabel())) {
            return false;
        }
        if (arg1.getFunctionId() != other.getFunctionId()) {
            return false;
        }
        if (!Objects.equals(arg1.getCallMode(), other.getCallMode())) {
            return false;
        }
        if (arg1.getCallIntervalInMillis() != other.getCallIntervalInMillis()) {
            return false;
        }
        if (!equalsFields(arg1.getFields(), other.getFields())) {
            return false;
        }
        if (!equalsParams(arg1.getRequestParams(), other.getRequestParams())) {
            return false;
        }
        if (!equalsParams(arg1.getResponseParams(), other.getResponseParams())) {
            return false;
        }
        return true;
    }

    private static boolean equalsParams(List<BlendingBondVsnTo> p1, List<BlendingBondVsnTo> p2) {
        if (p1.size() != p2.size()) {
            return false;
        }
        for (BlendingBondVsnTo b1 : p1) {
            boolean contains = false;
            for (BlendingBondVsnTo b2 : p2) {
                if (b1.getFieldId() == b2.getFieldId() && b1.getParamName().equals(b2.getParamName())) {
                    contains = true;
                }
            }
            if (!contains) {
                return false;
            }
        }
        return true;
    }

    private static boolean equalsFields(List<? extends FieldTo> f1, List<? extends FieldTo> f2) {
        if (f1.size() != f2.size()) {
            return false;
        }
        for (FieldTo b1 : f1) {
            boolean contains = false;
            for (FieldTo b2 : f2) {
                if (b1.equals(b2)) {
                    contains = true;
                }
            }
            if (!contains) {
                return false;
            }
        }
        return true;
    }
}
