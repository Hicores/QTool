package cc.hicore.Utils;

import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;

import java.util.List;

public class DNSParser {
    public interface RecordCallback{
        void onResult(List<String> result);
    }
    public static void getTXTRecord(String domain,RecordCallback callback) throws Exception{
        Resolver resolver = new SimpleResolver("114.114.114.114");
        Record queryRecord = Record.newRecord(Name.fromString(domain+"."), Type.TXT, DClass.IN);
        Message queryMessage = Message.newQuery(queryRecord);
        resolver.sendAsync(queryMessage).whenComplete((answer, ex)->{
            if (answer != null){
                List<Record> records = answer.getSection(1);
                for (Record record : records){
                    if (record instanceof TXTRecord){
                        TXTRecord txtRecord = (TXTRecord) record;
                        callback.onResult(txtRecord.getStrings());
                    }
                }
            }
        }).toCompletableFuture().get();
    }
}
