//import com.Tls.CooldownService;
//import com.Tls.HotpService;
//import com.Tls.RateLimiterService;
//import com.Tls.SoftLockService;
//
//import javax.ws.rs.*;
//import javax.ws.rs.core.Response;
//
//@Path("/otp")
//public class OtpResource {
//
//    private final RateLimiterService rateLimiter = new RateLimiterService();
//    private final CooldownService cooldown = new CooldownService();
//    private final SoftLockService lock = new SoftLockService();
//    private final HotpService hotp = new HotpService();
//    private final OtpDao dao;
//
//    public OtpResource(OtpDao dao) {
//        this.dao = dao;
//    }
//
//    // ================= SEND OTP =================
//    @POST
//    @Path("/send")
//    public Response send(@QueryParam("userId") String userId,
//                         @HeaderParam("X-Forwarded-For") String ip) throws Exception {
//
//        String clientIp = (ip != null) ? ip : "unknown";
//
//        if (!rateLimiter.allow("SEND_GLOBAL")) {
//            return Response.status(429).entity("Global limit reached").build();
//        }
//
//        if (!rateLimiter.allow("SEND_IP:" + clientIp)) {
//            return Response.status(429).entity("Too many from IP").build();
//        }
//
//        if (!rateLimiter.allow("SEND_USER:" + userId)) {
//            return Response.status(429).entity("Too many for user").build();
//        }
//
//        if (!cooldown.allow(userId)) {
//            return Response.status(429).entity("Wait 30 sec").build();
//        }
//
//        if (lock.isLocked(userId)) {
//            return Response.status(423).entity("User locked").build();
//        }
//
//        long expiry = System.currentTimeMillis() + (2 * 60 * 1000);
//
//        long counter = dao.incrementCounter(userId, expiry);
//
//        String secret = dao.getSecret(userId);
//
//        String otp = hotp.generate(secret, counter);
//
//        System.out.println("OTP: " + otp);
//
//        return Response.ok("OTP sent").build();
//    }
//
//    // ================= VERIFY OTP =================
//    @POST
//    @Path("/verify")
//    public Response verify(@QueryParam("userId") String userId,
//                           @QueryParam("otp") String otp,
//                           @HeaderParam("X-Forwarded-For") String ip) throws Exception {
//
//        String clientIp = (ip != null) ? ip : "unknown";
//
//        if (!rateLimiter.allow("VERIFY_GLOBAL")) {
//            return Response.status(429).entity("Global verify limit").build();
//        }
//
//        if (!rateLimiter.allow("VERIFY_IP:" + clientIp)) {
//            return Response.status(429).entity("Too many attempts from IP").build();
//        }
//
//        if (!rateLimiter.allow("VERIFY_USER:" + userId)) {
//            return Response.status(429).entity("Too many attempts for user").build();
//        }
//
//        if (lock.isLocked(userId)) {
//            return Response.status(423).entity("User locked").build();
//        }
//
//        long expiry = dao.getExpiry(userId);
//
//        if (System.currentTimeMillis() > expiry) {
//            return Response.status(400).entity("OTP expired").build();
//        }
//
//        long counter = dao.getCounter(userId);
//        String secret = dao.getSecret(userId);
//
//        String expected = hotp.generate(secret, counter);
//
//        if (expected.equals(otp)) {
//
//            dao.incrementAfterSuccess(userId);
//            lock.success(userId);
//
//            return Response.ok("SUCCESS").build();
//
//        } else {
//
//            lock.fail(userId);
//            return Response.status(400).entity("Invalid OTP").build();
//        }
//    }
//}