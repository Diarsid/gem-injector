/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package example.modules.workers.fifth;

import java.util.Objects;

import example.modules.FifthModule;
import example.modules.FourthModule;
import example.modules.SecondModule;
import example.modules.SixthModule;

/**
 *
 * @author Diarsid
 */
class FifthModuleImpl implements FifthModule {

    private SecondModule second;
    private SixthModule sixth;
    private FourthModule fourth;
    
    FifthModuleImpl(SecondModule second, FourthModule fourth, SixthModule sixth) {
        this.second = second;
        this.sixth = sixth;
        this.fourth = fourth;
    }
    
    @Override
    public void printInfo(){
        System.out.println("Fifth module says: " + sixth.getInfo());
    }   
    
    
    @Override
    public SixthModule getSixthModule(){
        return sixth;
    }
    
    @Override
    public SecondModule getSecondModule(){
        return second;
    }
    
    @Override
    public FourthModule getFourthModule(){
        return fourth;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.sixth);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FifthModuleImpl other = (FifthModuleImpl) obj;
        if (!Objects.equals(this.sixth, other.sixth)) {
            return false;
        }
        return true;
    }
    
    
}
