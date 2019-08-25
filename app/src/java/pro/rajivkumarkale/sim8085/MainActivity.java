package pro.rajivkumarle.sim8085;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener
{

	private Memory memory;
	
	private Button mWriteButton, mEditButton, mRunButton,
				   mRegisterButton, mPreviousButton, 
				   mNextButton, mEnterButton;
				   
	private TextView mTextView;
	private EditText mEditText;
	
	private boolean write_f, write_first_f, edit_f, run_f,
					edit_first_f, register_f;
	private int address = 0;
	
	private String[][] tempRegister;
	private int registerAddress;
	
	
	// variables for calculations in simulator
	// flags
	private boolean sign_flag = false, zero_flag = false, auxillary_carry_flag = false,
				   pairity_flag = false, carry_flag = false; 
	
	// registers
	private String a_register="00", b_register="00", c_register="00",
				   d_register="00", e_register="00", h_register="00",
				   l_register="00";
	
	private int program_counter, stack_pointer;
	private boolean executing;
	private boolean branch_flag = false;
				   
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		memory = Memory.get();
		
		initialiseViews();
		resetInternalFlags();
    }
	
	// Write program into memory
	private void write(){
		clear();
		resetInternalFlags();
		write_first_f = true;
		write_f = true;
		
		mEditText.setEnabled(true);
		setText("Enter Starting Address :");
	}
	
	private void edit(){
		clear();
		resetInternalFlags();
		edit_first_f = true;
		edit_f = true;
		
		mEditText.setEnabled(true);
		setText("Enter Starting Address");
	}
	
	private void register(){
		resetInternalFlags();
		register_f = true;
		
		registerAddress = 0;
		mEditText.setEnabled(false);
		registerArray();
		setText(tempRegister[0][0]+" --> "+tempRegister[0][1]);
	}
	
	private void run(){
		clear();
		resetInternalFlags();
		run_f = true;
		
		mEditText.setEnabled(true);
		setText("Enter Starting Address :");
	}
	
	private void previous(){
		if(edit_f){
			address = (address > 0) ? (address - 1) : address;
			setText(hexValue(address)+" : "+memory.getValue(address));
		}
		else if(register_f){
			registerAddress = (registerAddress >0) ? (registerAddress - 1) : registerAddress; 
			setText(tempRegister[registerAddress][0]+" --> "+tempRegister[registerAddress][1]);
		}
	}
	
	private void next(){
		if(edit_f){
			address = (address < 65535) ? (address + 1) : address;
			setText(hexValue(address)+" : "+memory.getValue(address));
		}
		else if(register_f){
			registerAddress = (registerAddress < 6) ? (registerAddress + 1) : registerAddress; 
			setText(tempRegister[registerAddress][0]+" --> "+tempRegister[registerAddress][1]);
		}
	}
	
	private void enter(){
		if(write_first_f){
			address = Integer.valueOf(mEditText.getText().toString(), 16).intValue();
			setText(hexValue(address)+" :");
			write_first_f = false;
		}
		else if(write_f){
			memory.saveOpcode(mEditText.getText().toString(), address);
			address++;
			if(mEditText.getText().toString().toUpperCase().equals("HLT")){
				write_f = false;
				setText("Select Next Operation");
				mEditText.setEnabled(false);
			}else{
			setText(hexValue(address)+" :");
			}
		}
		else if(edit_first_f){
			address = Integer.valueOf(mEditText.getText().toString(), 16).intValue();
			setText(hexValue(address)+" : "+memory.getValue(address));
			edit_first_f = false;
		}
		else if(edit_f){
			memory.saveOpcode(mEditText.getText().toString(), address);
			setText(hexValue(address)+" : "+memory.getValue(address));
		}
		else if(run_f){
			address = Integer.valueOf(mEditText.getText().toString(), 16).intValue();
			program_counter = address;
			stack_pointer = 65534;
			mEditText.setEnabled(false);
			execute();
			setText("Program Executed");
		}
	
		clear();
	}
	
	private void execute(){
		//Toast.makeText(this, "execute()", Toast.LENGTH_SHORT).show();
		executing = true;
		
		while(executing){
			executing = checkInstruction(memory.getOpcode(program_counter));
			if(!branch_flag)
				program_counter ++;
			else
				branch_flag = false;
		}
	}
	
	private boolean checkInstruction(String instruction){
		String code = instruction.trim().split(" ")[0];
        switch(code){
              case "MOV":
                  mov(instruction);
                  break;
              case "MVI":
                  mvi(instruction);
                  break;
              case "LXI":
                  lxi(instruction);
                  break;
              case "LDA":
                  lda(instruction);
                  break;
              case "STA":
                  sta(instruction);
                  break;
              case "LHLD":
                  lhld(instruction);
                  break;
              case "SHLD":
                  shld(instruction);
                  break;
              case "LDAX":
                  ldax(instruction);
                  break;
              case "STAX":
                  stax(instruction);
                  break;
              case "XCHG":
                  xchg(instruction);
                  break;
              case "ADD":
                  add(instruction);
                  break;
              case "ADC":
                  adc(instruction);
                  break;
             case "ADI":
                  adi(instruction);
                  break;
              case "ACI":
                  aci(instruction);
                  break;
              case "DAD":
                  dad(instruction);
                  break;
              case "SUB":
                  sub(instruction);
                  break;
              case "SBB":
                  sbb(instruction);
                  break;
              case "SUI":
                  sui(instruction);
                  break;
              case "INR":
                  inr(instruction);
                  break;
              case "DCR":
                  dcr(instruction);
                  break;
              case "INX":
                  inx(instruction);
                  break;
              case "DCX":
                  dcx(instruction);
                  break;
              case "DAA":
                  daa(instruction);
                  break;
              case "ANA":
                  ana(instruction);
                  break;
              case "ANI":
                  ani(instruction);
                  break;
              case "ORA":
                  ora(instruction);
                  break;
              case "ORI":
                  ori(instruction);
                  break;
              case "XRA":
                  xra(instruction);
                  break;
              case "XRI":
                  xri(instruction);
                  break;
              case "CMA":
                  cma();
                  break;
              case "CMC":
                  cmc();
                  break;
              case "STC":
                  stc();
                  break;
//            case "CMP":
//                perform_cmp(instruction);
//                break;
//            case "CPI":
//                perform_cpi(instruction);
//                break;
//            case "RLC":
//                perform_rlc(instruction);
//                break;
//            case "RRC":
//                perform_rrc(instruction);
//                break;
//            case "RAL":
//                perform_ral(instruction);
//                break;
//            case "RAR":
//                perform_rar(instruction);
//                break;
              case "JMP":
                  jmp(instruction);
                  break;
              case "JZ":
                  jz(instruction);
                  break;
              case "JNZ":
                  jnz(instruction);
                  break;
              case "JC":
                  jc(instruction);
                  break;
              case "JNC":
                  jnc(instruction);
                  break;
              case "JP":
                  jp(instruction);
                  break;
              case "JM":
                  jm(instruction);
                  break;
              case "JPE":
                  jpe(instruction);
                  break;
              case "JPO":
                  jpo(instruction);
                  break;
//            case "CALL":
//                perform_call(instruction);
//                break;
//            case "CZ":
//                perform_cz(instruction);
//                break;
//            case "CNZ":
//                perform_cnz(instruction);
//                break;
//            case "CC":
//                perform_cc(instruction);
//                break;
//            case "CNC":
//                perform_cnc(instruction);
//                break;
//            case "CP":
//                perform_cp(instruction);
//                break;
//            case "CM":
//                perform_cm(instruction);
//                break;
//            case "CPE":
//                perform_cpe(instruction);
//                break;
//            case "CPO":
//                perform_cpo(instruction);
//                break;
//            case "RET":
//                perform_ret(instruction);
//                break;
//            case "RZ":
//                perform_rz(instruction);
//                break;
//            case "RNZ":
//                perform_rnz(instruction);
//                break;
//            case "RC":
//                perform_rc(instruction);
//                break;
//            case "RNC":
//                perform_rnc(instruction);
//                break;
//            case "RP":
//                perform_rp(instruction);
//                break;
//            case "RM":
//                perform_rm(instruction);
//                break;
//            case "RPE":
//                perform_rpe(instruction);
//                break;
//            case "RPO":
//                perform_rpo(instruction);
//                break;
              case "PCHL":
                  pchl(instruction);
                  break;
              case "PUSH":
                  push(instruction);
                  break;
              case "POP":
                  pop(instruction);
                  break;
              case "XTHL":
                  xthl(instruction);
                  break;
              case "SPHL":
                  sphl(instruction);
                  break;
			  case "HLT":
				  return false;
        }
		return true;
		
	} 
	
	
	//CMA Command
	/*
	 * Complement the Accumulator
	 */
	 
    private void cma(){
        int val = intValue(a_register);
        String bin = Integer.toBinaryString(val);
        for(int i = bin.length(); i < 8; i++)
            bin = "0" + bin;
        val = 0;
        for(int i = 7; i >= 0; i--)
            if(bin.charAt(i)=='0')
                val += (Math.pow(2,7-i));
		a_register = hex8bit(val);
    }



    //CMC Command
	/*
	 * Complement the carry flag
	 */ 
	 
    private void cmc(){
        carry_flag = !carry_flag;
    }



    //STC Command
	/*
	 * Set the carry flag
	 */ 
	 
    private void stc(){
        carry_flag = true;
    }
	
	
	//XRA Command
	/*
	 * Type 1 Xor the A with register
	 * Type 2 Xor the A with memory
	 */

    //Call the appropriate method acc to type
    private void xra(String passed){
        int type = xra_type(passed);
        switch(type){
            case 1:
                xra_with_reg(passed);
                break;
            case 2:
                xra_with_mem(passed);
                break;
        }
    }

    //Find the type of ORA
    private int xra_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }

    //Type 1 of ORA
    private void xra_with_reg(String passed){
        int val1 = intValue(a_register);
        int val2 = intValue(getRegValue(passed.charAt(4)));
        val1 = val1 ^ val2;
		a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = false;
    }

    //Type 2 of ORA
    private void xra_with_mem(String passed){
        int val1 = intValue(a_register);
        int val2 = intValue(memory.getOpcode(intValue(hl_address())));
        val1 = val1 ^ val2;
		a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = false;
    }



    //XRI Command
	/*
	 * Xri the immediate data to the A
	 */

    private void xri(String passed){
        int val1 = intValue(a_register);
		program_counter ++;
        int val2 = intValue(memory.getOpcode(program_counter));
        val1 = val1 ^ val2;
        a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = false;
    }

	
	
	
	
	//ORA Command
	/*
	 * Type 1 Or the A with register
	 * Type 2 Or the A with memory
	 */

    //Call the appropriate method acc to type
    private void ora(String passed){
        int type = ora_type(passed);
        switch(type){
            case 1:
                ora_with_reg(passed);
                break;
            case 2:
                ora_with_mem(passed);
                break;
        }
    }

    //Find the type of ORA
    private int ora_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }

    //Type 1 of ORA
    private void ora_with_reg(String passed){
        int val1 = intValue(a_register);
        int val2 = intValue(getRegValue(passed.charAt(4)));
        val1 = val1 | val2;
		a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = false;
    }

    //Type 2 of ORA
    private void ora_with_mem(String passed){
        int val1 = intValue(a_register);
        int val2 = intValue(memory.getOpcode(intValue(hl_address())));
        val1 = val1 | val2;
		a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = false;
    }



    //ORI Command
	/*
	 * Or the immediate data to the A
	 */
	 
    private void ori(String passed){
        int val1 = intValue(a_register);
		program_counter ++;
        int val2 = intValue(memory.getOpcode(program_counter));
        val1 = val1 | val2;
        a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = false;
    }
	
	
	
	//ANA Command
	/*
	 * Type 1 And the A with register
	 * Type 2 And the A with memory
	 */

    //Call the appropriate method acc to type
    private void ana(String passed){
        int type = ana_type(passed);
        switch(type){
            case 1:
                ana_with_reg(passed);
                break;
            case 2:
                ana_with_mem(passed);
                break;
        }
    }

    //Find the type of ANA
    private int ana_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }

    //Type 1 of ANA
    private void ana_with_reg(String passed){
        int val1 = intValue(a_register);
        int val2 = intValue(getRegValue(passed.charAt(4)));
        val1 = val1 & val2;
		a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = true;
    }

    //Type 2 of ANA
    private void ana_with_mem(String passed){
        int val1 = intValue(a_register);
        int val2 = intValue(memory.getOpcode(intValue(hl_address())));
        val1 = val1 & val2;
		a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = true;
    }



    //ANI Command
	/*
	 * And the immediate data to the A
	 */

    //Call the appropriate method acc to type of ANI
    private void ani(String passed){
		int val1 = intValue(a_register);
		program_counter ++;
        int val2 = intValue(memory.getOpcode(program_counter));
        val1 = val1&val2;
        a_register = hex8bit(val1);
		modifyFlags();
		carry_flag = false;
		auxillary_carry_flag = true;
    }

	
	
	
	
	//CALL Commands
	
	
	//JMP Commands
	private void jmp(String passed){
		branch_flag = true;
		program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		
	}
	
	private void jc(String passed){
		branch_flag = true;
		if(carry_flag)
			program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		else	
			program_counter += 2;
	}
	
	private void jnc(String passed){
		branch_flag = true;
		if(!carry_flag)
			program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		else
			program_counter += 2;
	}
	
	private void jp(String passed){
		branch_flag = true;
		if(!sign_flag)
			program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		else
			program_counter += 2;
	}
	
	private void jm(String passed){
		branch_flag = true;
		if(sign_flag)
			program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		else	
			program_counter += 2;
	}
	
	private void jpe(String passed){
		branch_flag = true;
		if(pairity_flag)
			program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		else	
			program_counter += 2;
	}
	
	private void jpo(String passed){
		branch_flag = true;
		if(!pairity_flag)
			program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		else	
			program_counter += 2;
	}
	
	private void jz(String passed){
		branch_flag = true;
		if(zero_flag)
			program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		else	
			program_counter += 2;
	}
	
	private void jnz(String passed){
		branch_flag = true;
		if(!zero_flag)
			program_counter = intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1));
		else
			program_counter += 2;
	}
	
	//PUSH Command
	/*
	 * Push the register pair data on to the stack
	 */
    private void push(String passed){
        char r = passed.charAt(5);
        switch(r){
            case 'B':
                fill_the_stack(b_register , c_register);
                break;
            case 'D':
                fill_the_stack(d_register , e_register);
                break;
            case 'H':
                fill_the_stack(h_register , l_register);
                break;
            case 'P':
                fill_the_stack(a_register, hex8bit(psw()));
                break;
        }
    }

	 //Fill the stack
    private void fill_the_stack(String h,String l){
        memory.saveData(h, --stack_pointer);
        memory.saveData(l, --stack_pointer);
    }
	
	//Get the Program Status Word
    private int psw(){
        int psw = 0;
        psw+=(carry_flag ? 1 : 0);
        psw+=(pairity_flag ? 4 : 0);
        psw+=(auxillary_carry_flag ? 16 : 0);
        psw+=(zero_flag ? 64 : 0);
        psw+=(sign_flag ? 128 : 0);
        return psw;
    }


    //POP Command
	/*
	 * Pop the contents of the stack
	 */
    private void pop(String passed){
        char r = passed.charAt(4);
        switch(r){
            case 'B':
				c_register = memory.getOpcode(stack_pointer++);
				b_register = memory.getOpcode(stack_pointer++);  
                break;
            case 'D':
                e_register = memory.getOpcode(stack_pointer++);
				d_register = memory.getOpcode(stack_pointer++);  
                break;
            case 'H':
                l_register = memory.getOpcode(stack_pointer++);
				h_register = memory.getOpcode(stack_pointer++);  
                break;
            case 'P':
                String w = memory.getOpcode(stack_pointer++);
                h_register = memory.getOpcode(stack_pointer++);
                w = Integer.toBinaryString(intValue(w));
                carry_flag = (w.charAt(0)=='1') ? true : false;
                pairity_flag = (w.charAt(2)=='1') ? true : false;
                auxillary_carry_flag = (w.charAt(4)=='1') ? true : false;
                zero_flag = (w.charAt(6)=='1') ? true : false;
                sign_flag = (w.charAt(7)=='1') ? true : false;
        }
    }

 
	
	//SPHL Command
	/*
	 * Exchange the content of stack with HL pair
	 */
	private void sphl(String passed){
		String s = hexValue(stack_pointer);
		String upper_byte = "", lower_byte = "";
		if(s.length() > 4){
			lower_byte = s.substring(s.length() - 4);
			if(s.length() > 8){
				upper_byte = s.substring(s.length() - 8, s.length() - 4);
			}else{
				upper_byte = s.substring(0, s.length() - 4);
			}
		}else{
			lower_byte = s;
			upper_byte = "00";
		}
		
		s = h_register;
		h_register = upper_byte;
		upper_byte = s;
		
		s = l_register;
		l_register = lower_byte;
		lower_byte = s;
		
		stack_pointer = intValue(upper_byte + lower_byte);
	}
	
	//PCHL Command
	/*
	 * Jump to address given by HL pair
	 */
	private void pchl(String passed){
		program_counter = intValue(hl_address());
	}
	
	//XTHL Command
	/*
	 * Exchange the content of stack with HL pair
	 */
	 private void xthl(String passed){
		String s = l_register;
		l_register = memory.getOpcode(stack_pointer);
		memory.saveData(s, stack_pointer);
		
		s = h_register;
		h_register = memory.getOpcode(stack_pointer + 1);
		memory.saveData(s, stack_pointer + 1);
	 }
	
	
	private void modifyFlags(){
		// zero flag
		  zero_flag = ( intValue(a_register) == 0) ? true : false;
		  
		// sign flag
		  sign_flag = (((intValue(a_register) >> 7) & 1) == 1) ? true: false;
		  
		//pairity flag
		  int count = 0;
		  int num  = intValue(a_register);
		  while(num != 0){
			  count = ((num & 1) == 1) ? (count + 1) : count;
			  num = num >> 1;
		  }
		  pairity_flag = ((count % 2) == 0) ? true : false;
		
	}
	
	//DAA Command
	/*
	 * Adjust the content of the A to represent decimel value
	 */

    private void daa(String passed){
		String s = a_register;
		String l_nibble = "0", h_nibble = "0";
		int val = 0;
		if(s.length() == 1){
			l_nibble = s;
		}else{
			l_nibble = s.charAt(1)+"";
			h_nibble = s.charAt(0)+"";
		}
        if(intValue(l_nibble) > 9 || auxillary_carry_flag){
			if(intValue(h_nibble) > 9 || carry_flag){
				val = intValue(a_register) + intValue("66");
			}else{
				val = intValue(a_register) + intValue("06");
			}
		}else{
			if(intValue(h_nibble) > 9 || carry_flag){
				val = intValue(a_register) + intValue("60");
			}else{
				val = intValue(a_register);
			}
		}
		carry_flag = (val > 255) ? false : true;
        a_register = hex8bit(val);
    }
	
	
	
	//INX Command
	/*
	 * Type 1 Increment the data contained in the register pair
	 * Type 2 Increment the content of stack pointer
	 */

    private void inx(String passed){
        int type = inx_type(passed);
        switch(type){
            case 1:
                inx_rp(passed);
                break;
			case 2:
				inx_sp();
        }
    }

    //Find the type of INX
    private int inx_type(String passed){
        if(passed.charAt(4) == 'S'){
			return 2;
		}
		else{
			return 1;
		}
    }

    //Type 1 of INX
    private void inx_rp(String passed){
        int h,l;
        switch(passed.charAt(4)){
            case 'B':
                h = intValue(b_register);
                l = intValue(c_register);
                l++;
                h += ((l > 255) ? 1 : 0 );
				if(h > 255){
					h = l = 0;
				}
				inputInRegister('C', hex8bit(l));
				inputInRegister('B', hex8bit(h));
                break;
            case 'D':
                h = intValue(d_register);
                l = intValue(e_register);
                l++;
                h += ((l > 255) ? 1 : 0 );
				if(h > 255){
					h = l = 0;
				}
				inputInRegister('E', hex8bit(l));
				inputInRegister('D', hex8bit(h));
            case 'H':
				h = intValue(h_register);
                l = intValue(l_register);
                l++;
                h += ((l > 255) ? 1 : 0 );
				if(h > 255){
					h = l = 0;
				}
				inputInRegister('L', hex8bit(l));
				inputInRegister('H', hex8bit(h));
        }
    }
	
	// Type 2 of INX
	private void inx_sp(){
		if(stack_pointer != 255){
			stack_pointer ++;
		}
		else{
			stack_pointer = 0;
		}
	}


    //DCX Command
	/*
	 * Type 1 Decrement the data contained in the register pair
	 * Type 2 Decrement stack pointer's data;
	 */
    private void dcx(String passed){
        int type = dcx_type(passed);
        switch(type){
            case 1:
                dcx_rp(passed);
                break;
			case 2:
				dcx_sp();
        }
    }

    //Find the type of DCX
    static int dcx_type(String passed){
        return 1;
    }

    //Type 1 of DCX
    private void dcx_rp(String passed){
        int h,l;
        switch(passed.charAt(4)){
            case 'B':
				h = intValue(b_register);
                l = intValue(c_register);
                l--;
                if(l == -1){
                    h --;
                    l = 255;
				}
                if( h == -1){
                    h = 255;
				}
				inputInRegister('C', hex8bit(l));
				inputInRegister('B', hex8bit(h));
                break;
            case 'D':
                h = intValue(d_register);
                l = intValue(e_register);
                l--;
                if(l == -1){
                    h --;
                    l = 255;
				}
                if( h == -1){
                    h = 255;
				}
				inputInRegister('E', hex8bit(l));
				inputInRegister('D', hex8bit(h));
                break;
            case 'H':
                h = intValue(h_register);
                l = intValue(l_register);
                l--;
                if(l == -1){
                    h --;
                    l = 255;
				}
                if( h == -1){
                    h = 255;
				}
				inputInRegister('L', hex8bit(l));
				inputInRegister('H', hex8bit(h));
                break;
        }
    }
	
	// Type 2 of DCX
	private void dcx_sp(){
		if(stack_pointer != 0){
			stack_pointer --;
		}
		else{
			stack_pointer = 255;
		}
	}
	
	
	//INR Command
	/*
	 * Type 1 Increment the value of registers
	 * Type 2 Increment the value of the memory location
	 */
    private void inr(String passed){
        int type = inr_type(passed);
        switch(type){
            case 1:
                inr_reg(passed);
                break;
            case 2:
                inr_mem(passed);
                break;
        }
    }

    //Find the type of INR
    private int inr_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }

    //Type 1 of INR
    private void inr_reg(String passed){
        int val = intValue(getRegValue(passed.charAt(4)));
        auxillary_carry_flag = isAuxillaryCarry(lowerNibble(getRegValue(passed.charAt(4))) + 1);
		val = (val != 255) ? (val + 1) : 0;
	    inputInRegister(passed.charAt(4), hex8bit(val));
		modifyFlags();     
    }

    //Type 2 of INR
    private void inr_mem(String passed){
        int val = intValue(memory.getOpcode(intValue(hl_address())));
        auxillary_carry_flag = isAuxillaryCarry(lowerNibble(memory.getOpcode(intValue(hl_address()))) + 1);
		val = (val != 255) ? (val + 1) : 0;
	    inputInRegister(passed.charAt(4), hex8bit(val));
		modifyFlags();  
    }



    //DCR Command
	/*
	 * Type 1 Decrement the value of registers
	 * Type 2 Decrement the value of the memory location
	 */

    private void dcr(String passed){
        int type = dcr_type(passed);
        switch(type){
            case 1:
                dcr_reg(passed);
                break;
            case 2:
                dcr_mem(passed);
                break;
        }
    }

    //Find the type of INR
    private int dcr_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }

    //Type 1 of DCR
    private void dcr_reg(String passed){
		int val = intValue(getRegValue(passed.charAt(4)));
        val = (val != 0) ? (val - 1) : 255;
	    inputInRegister(passed.charAt(4), hex8bit(val));
		modifyFlags();     
    }

    //Type 2 of DCR
    private void dcr_mem(String passed){
		int val = intValue(memory.getOpcode(intValue(hl_address())));
		val = (val != 0) ? (val - 1) : 255;
	    inputInRegister(passed.charAt(4), hex8bit(val));
		modifyFlags();
	}
	
	
	
	//SBB Command
	/*
	 * Type 1 Subtract register and carry from A
	 * Type 2 Subtract memory content and carry from A
	 */
	 
    private void sbb(String passed){
        int type = sbb_type(passed);
        switch(type){
            case 1:
                sbb_reg(passed);
                break;
            case 2:
                sbb_mem(passed);
                break;
        }
    }

    //Find the type of SBB
    private int sbb_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }

    //Type 1 of SBB
    private void sbb_reg(String passed){
        int subt = intValue(a_register);
        int mult = intValue(getRegValue(passed.charAt(4)));
        mult++;
        mult%=256;
        mult = 256-mult;
        mult%=256;
        subt+=mult;
        carry_flag = (subt > 255) ? false : true;
		a_register = hex8bit(subt);
		modifyFlags();
       } 

    //Type 2 of SBB
    private void sbb_mem(String passed){
		int subt = intValue(a_register);
        int mult = intValue(memory.getOpcode(intValue(hl_address())));
        mult++;
        mult%=256;
        mult = 256-mult;
        subt+=mult;
        carry_flag = (subt > 255) ? false : true;
		a_register = hex8bit(subt);
		modifyFlags();
    }


    //SUI Command
	/*
	 * Type 1 Subtract immediate data from A
	 */
    private void sui(String passed){
		program_counter ++;
		int subt = intValue(a_register);
        int minu = intValue(memory.getOpcode(program_counter));
        minu = 256-minu;
        minu%=256;
        subt+=minu;
		carry_flag = (subt > 255) ? false : true;
		a_register = hex8bit(subt);
		modifyFlags();
    }


	//SUB Command
	/*
	 * Type 1 Subtract register from A
	 * Type 2 Subtract the memory data from A
	 */
    private void sub(String passed){
        int type = sub_type(passed);
        switch(type){
            case 1:
                sub_reg(passed);
                break;
            case 2:
                sub_mem(passed);
                break;
        }
    }

    //Find the type of SUB
    private int sub_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }

    //Type 1 of SUB
    private void sub_reg(String passed){
        int subt = intValue(a_register);
        int minu = intValue(getRegValue(passed.charAt(4)));
        minu = 256-minu;
        minu%=256;
        subt+=minu;
        carry_flag = (subt > 255) ? false : true;
		a_register = hex8bit(subt);
		modifyFlags();
    }

    //Type 2 of SUB
    private void sub_mem(String passed){
		int subt = intValue(a_register);
        int minu = intValue(memory.getOpcode(intValue(hl_address())));
        minu = 256-minu;
        minu%=256;
        subt+=minu;
        carry_flag = (subt > 255) ? false : true;
		a_register = hex8bit(subt);
		modifyFlags();
    }
	
	
	//DAD Command
	/*
	 * Add the contents of the HL pair to the mentioned register pair
	 */
	private void dad(String passed){
		switch(passed.charAt(4)){
            case 'B':
                dad_with_hl( intValue(b_register), intValue(c_register));
                break;
            case 'D':
                dad_with_hl( intValue(d_register), intValue(e_register));
				break;
			case 'H':
				dad_with_hl( intValue(h_register), intValue(l_register));
				break;
			case 'S':
				dad_with_sp();
        }
	}
	
	//Function to implement dad_with_hl
    private void dad_with_hl(int h,int l){
        l += intValue(l_register);
        int carry = (l > 255) ? 1 : 0;
        l_register = hex8bit(l);
        h += intValue(h_register) + carry;
		h_register = hex8bit(h);
        carry_flag = (h > 255) ? true : false;
    }
	
	private void dad_with_sp(){
		int hl = intValue(h_register + l_register);
		hl += stack_pointer;
		carry_flag = (hl > 65535) ? true : false;
		String s = Integer.toBinaryString(hl);
		int length = s.length();
		s = (length >= 16 ) ? s.substring(length - 16) : s;
		s = hexValue(Integer.valueOf(s,2).intValue());
		
		if( s.length() > 2){
			l_register = s.substring( s.length() - 2);
			if(s.length() >= 4){
				h_register = s.substring(s.length() - 4, s.length() -2);
			}else{
				h_register = "0" + s.charAt(0);
			}
		}else{
			l_register = s;
			h_register = "00";
		}
		
	}
	
	
	//Find type of ADD
    private int add_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }
	
	//Call appropriate method acc to type
	private void add(String passed){
		int type = add_type(passed);
		if(type==1)
            add_with_reg(passed);
        else if(type==2)
            add_with_mem(passed);
	}
	
	//Type 1 of ADD
    private void add_with_reg(String passed){
        int num = intValue(a_register) + intValue(getRegValue(passed.charAt(4)));
		auxillary_carry_flag = isAuxillaryCarry(lowerNibble(a_register) + lowerNibble(getRegValue(passed.charAt(4))));
		carry_flag = (num > 255) ? true : false;
		a_register = hex8bit(num);
		modifyFlags();
    }
    //Type 2 of ADD
    private void add_with_mem(String passed){
        int num = intValue(a_register) + intValue(memory.getOpcode(intValue(hl_address())));
		auxillary_carry_flag = isAuxillaryCarry(lowerNibble(a_register) + lowerNibble(memory.getOpcode(intValue(hl_address()))));
		carry_flag = (num > 255) ? true : false;
		a_register = hex8bit(num);
		modifyFlags();
    }
	
	//ADI Command
	/*
	 * Add the immediate data to the A
	 */
	private void adi(String passed){
		program_counter ++;
		int num = intValue(a_register) + intValue(memory.getOpcode(program_counter));
		auxillary_carry_flag = isAuxillaryCarry(lowerNibble(a_register) + lowerNibble(memory.getOpcode(program_counter)));
		carry_flag = (num > 255) ? true : false;
		a_register = hex8bit(num);
		modifyFlags();
	}
	
	
	//ADC Command
	/*
	 * Type 1 Add register with carry to A
	 * Type 2 Add memory with carry to A
	 */
	 
	private void adc(String passed){
		int type = adc_type(passed);
		if(type==1)
            adc_with_reg(passed);
        else if(type==2)
            adc_with_mem(passed);
	}
	
	//Find type of ADC
    private int adc_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else
            return 1;
    }
	
	//Type 1 of ADC
    private void adc_with_reg(String passed){
        int num = intValue(a_register) + intValue(getRegValue(passed.charAt(4))) + ((carry_flag) ? 1 : 0);
		auxillary_carry_flag = isAuxillaryCarry(lowerNibble(a_register) + lowerNibble(getRegValue(passed.charAt(4))) +((carry_flag) ? 1 : 0));
		carry_flag = (num > 255) ? true : false;
		a_register = hex8bit(num);
		modifyFlags();
    }
    //Type 2 of ADC
    private void adc_with_mem(String passed){
        int num = intValue(a_register) + intValue(memory.getOpcode(intValue(hl_address()))) + ((carry_flag) ? 1 : 0);
		auxillary_carry_flag = isAuxillaryCarry(lowerNibble(a_register) + lowerNibble(memory.getOpcode(intValue(hl_address()))) +((carry_flag) ? 1 : 0));
		carry_flag = (num > 255) ? true : false;
		a_register = hex8bit(num);
		modifyFlags();
    }
	
	//ACI Command
	/*
	 * Add the immediate data with the accumulator with carry
	 */
	private void aci(String passed){
		program_counter ++;
		int num = intValue(a_register) + intValue(memory.getOpcode(program_counter)) + ((carry_flag) ? 1 : 0);
		auxillary_carry_flag = isAuxillaryCarry(lowerNibble(a_register) + lowerNibble(memory.getOpcode(program_counter)) +((carry_flag) ? 1 : 0));
		carry_flag = (num > 255) ? true : false;
		a_register = hex8bit(num);
		modifyFlags();
	}
	
	
	//XCHG Command
	/*
	 * Exchange the contents of DE and HL reg pair
	 */
  
    private void xchg(String passed){
       String temp = h_register;
	   h_register = d_register;
	   d_register = temp;
	   
	   temp = l_register;
	   l_register = e_register;
	   e_register = temp;
    }
   
	
	//STAX Command
	/*
	 * Store the content of A to the memory location specified by the content of the  B/D register pair
	 */

    private void stax(String passed){
        char c = passed.charAt(5);
		if(c == 'B')
			memory.saveData(a_register, intValue(b_register + c_register));
		else if(c == 'D')
			memory.saveData(a_register, intValue(d_register + e_register));
    }
    
	
	//LDAX Command
	/*
	 *  Load the A with the data from the memory having address as register pair content
	 */

    private void ldax(String passed){
		char c = passed.charAt(5);
		if(c == 'B')
           a_register = memory.getOpcode(intValue(b_register + c_register));
		else if(c == 'D')
		   a_register = memory.getOpcode(intValue(d_register + e_register));
    }
    
	
	//SHLD Command
	/*
	   Store the data from HL pair to memory
	 */

    private void shld(String passed){
        String s = memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1);
		memory.saveData(l_register, intValue(s));
		memory.saveData(h_register, intValue(s) + 1);
		program_counter += 2;
    }
	
	
	//LHLD Command
	/*
	   Load the data from consecutive memory to HL pair direct
	 */

    private void lhld(String passed){
		String s = memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1);
		l_register = memory.getOpcode(intValue(s));
		h_register = memory.getOpcode(intValue(s) + 1);
		program_counter += 2;
	}
	
	
	//STA Command
	/*
	 Store the data from A to mem
	 */

    private void sta(String passed){
        memory.saveOpcode(a_register, intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1)));
		program_counter += 2;
    }
    
	
	//LDA Command
	/*
	 Load the data from mem to A
	 */ 
	 
     private void lda(String passed){
		 a_register = memory.getOpcode(intValue(memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1)));
    	 program_counter += 2;
	}
    
   
	
	//LXI Command
	/*
	 Type 1 Load immediately the data into the register pair
	 */
	
	//Call appropriate method acc to type
    private void lxi(String passed){
        int type = lxi_type(passed);
        if(type==1)
            lxi_to_reg_pair(passed);
		else if(type == 2){
			lxi_to_stack_pointer();
		}
    }
    //Find type of LXI
    private int lxi_type(String passed){
        char c = passed.charAt(4);
        if(c != 'S')
            return 1;
        else
            return 2;
    }
    //Type 1 of LXI
    private void lxi_to_reg_pair(String passed){
        char c = passed.charAt(4);
		program_counter ++;
        if(c == 'B'){
            inputInRegister('C',memory.getOpcode(program_counter));
			program_counter ++;
			inputInRegister('B', memory.getOpcode(program_counter));
        }
        else if(c == 'D'){
            inputInRegister('E',memory.getOpcode(program_counter));
			program_counter ++;
			inputInRegister('D', memory.getOpcode(program_counter));
        }
        else if(c == 'H'){
            inputInRegister('L',memory.getOpcode(program_counter));
			program_counter ++;
			inputInRegister('H', memory.getOpcode(program_counter));
        }
    }
	
	// Type 2 of LXI
	private void lxi_to_stack_pointer(){
		stack_pointer = intValue( memory.getOpcode(program_counter + 2) + memory.getOpcode(program_counter + 1) );
		program_counter  += 2;
	}
	
	
	//MVI Command
	/*
	 Type 1 mvi to reg
	 Type 2 mvi to mem
	 */

    //Call appropriate method acc to type
    private void mvi(String passed){
        int type = mvi_type(passed);
        if(type==1)
            mvi_reg(passed);
        else if(type==2)
            mvi_mem(passed);
    }
    //Find type of mvi
    private int mvi_type(String passed){
        if(passed.charAt(4)!='M')
            return 1;
        else 
            return 2;
    }
    //Type 1 of MVI
    private void mvi_reg(String passed){
		program_counter ++;
        inputInRegister( passed.charAt(4), memory.getOpcode(program_counter));
    }
	
    //Type 2 of MVI
    private void mvi_mem(String passed){
		program_counter ++;
        int memory_address = intValue(hl_address());
		memory.saveData(memory.getOpcode(program_counter), memory_address);
    }
	
	
	//MOV Command

	/*
	 Type 1 memory to reg
	 Type 2 reg to memory
	 Type 3 reg to reg
	 */
	 
    //This will call the appropriate function for that MOV
    private void mov(String passed){
        int type = mov_type(passed);
        if(type==1)
            mov_memory_to_reg(passed);
        else if(type==2)
            mov_reg_to_memory(passed);
        else if(type==3)
            mov_reg_to_reg(passed);
    }
	
    //This will find the type of MOV
    private int mov_type(String passed){
        if(passed.charAt(4)=='M')
            return 2;
        else if(passed.charAt(7)=='M')
            return 1;
        else
            return 3;
    }
	
    //Type 1 of MOV
    private void mov_memory_to_reg(String passed){
		inputInRegister(passed.charAt(4), memory.getOpcode(intValue(hl_address())));
    }
    //Type 2 of MOV
    private void mov_reg_to_memory(String passed){
		memory.saveData(getRegValue(passed.charAt(7)) ,intValue(hl_address()));
    }
    //Type 3 of MOV
    private void mov_reg_to_reg(String passed){
		inputInRegister(passed.charAt(4),getRegValue(passed.charAt(7))); 
    }
	
	
	
	private void initialiseViews(){
		mWriteButton = (Button) findViewById(R.id.id_write_button);
		mWriteButton.setOnClickListener(this);
		mEditButton = (Button) findViewById(R.id.id_edit_button);
		mEditButton.setOnClickListener(this);
		mEnterButton = (Button) findViewById(R.id.id_enter_button);
		mEnterButton.setOnClickListener(this);
		mRegisterButton = (Button) findViewById(R.id.id_register_button);
		mRegisterButton.setOnClickListener(this);
		mRunButton = (Button) findViewById(R.id.id_run_button);
		mRunButton.setOnClickListener(this);
		mPreviousButton = (Button) findViewById(R.id.id_previous_button);
		mPreviousButton.setOnClickListener(this);
		mNextButton = (Button) findViewById(R.id.id_next_button);
		mNextButton.setOnClickListener(this);
		mEditText = (EditText) findViewById(R.id.id_edittext);
		mTextView = (TextView) findViewById(R.id.id_text_view);
		
		mEditText.setEnabled(false);
	}
	
	@Override
	public void onClick(View view)
	{
		switch(view.getId()){
			case R.id.id_write_button:
				write();
				break;
			case R.id.id_edit_button:
				edit();
				break;
			case R.id.id_register_button:
				register();
				break;
			case R.id.id_run_button:
				run();
				break;
			case R.id.id_previous_button:
				previous();
				break;
			case R.id.id_next_button:
				next();
				break;
			case R.id.id_enter_button:
				enter();
				break;
		}
	}
	
	private String hl_address(){
		return h_register+l_register;
	}
	
	private void setText(String s){
		mTextView.setText(s);
	}
	
	private String hexValue(int num){
		return Integer.toHexString(num).toUpperCase();
	}
	
	private int intValue(String s){
		return Integer.valueOf(s, 16).intValue();
	}
	
	private void clear(){
		mEditText.setText("");
	}
	
	private void resetInternalFlags(){
		edit_first_f = false;
		edit_f = false;
		write_first_f = false;
		write_f = false;
		run_f = false;
		register_f = false;
	}
	
	// insert register's value into array for looking
	private void registerArray(){
		String[][] temp = new String[7][2];
		temp[0][0] = "A"; temp[0][1] = a_register;
		temp[1][0] = "B"; temp[1][1] = b_register;
		temp[2][0] = "C"; temp[2][1] = c_register;
		temp[3][0] = "D"; temp[3][1] = d_register;
		temp[4][0] = "E"; temp[4][1] = e_register;
		temp[5][0] = "H"; temp[5][1] = h_register;
		temp[6][0] = "L"; temp[6][1] = l_register;
		
		tempRegister = temp;
	}
	
	private void inputInRegister(char c, String s){
		switch(c){
			case 'A':
				a_register = s;
				break;
			case 'B':
				b_register = s;
				break;
			case 'C':
				c_register = s;
				break;
			case 'D':
				d_register = s;
				break;
			case 'E':
				e_register = s;
				break;
			case 'H':
				h_register = s;
				break;
			case 'L':
				l_register = s;
				break;
		}
	}
	
	private String getRegValue(char c){
		String s = "";
		switch(c){
			case 'A':
				s = a_register;
				break;
			case 'B':
				s = b_register;
				break;
			case 'C':
				s = c_register;
				break;
			case 'D':
				s = d_register;
				break;
			case 'E':
				s = e_register;
				break;
			case 'H':
				s = h_register;
				break;
			case 'L':
				s = l_register;
				break;
		}
		return s;
	}
	
	private String hex8bit(int num){
		String s = Integer.toBinaryString(num);
		if(s.length() > 8){
			s = s.substring(s.length() - 8);
		}
		s = hexValue(Integer.valueOf(s,2).intValue());
		return s;
	}
	
	private int lowerNibble(String s){
		if(s.length() == 2){
			return intValue( s.charAt(1) + "");
		}
		return intValue(s);
	}
	
	private boolean isAuxillaryCarry(int num){
		if(num > 15){
			return true;
		}
		return false;
	}
	
}
