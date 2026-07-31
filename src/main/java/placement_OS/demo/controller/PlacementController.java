package placement_OS.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import placement_OS.demo.entity.PlacementRequest;
import placement_OS.demo.response.ApiResponse;
import placement_OS.demo.service.PlacementService;

import java.util.List;

@RestController
@RequestMapping("/placement")
public class PlacementController {

    private final PlacementService placementService;

    public PlacementController(PlacementService placementService) {
        this.placementService = placementService;
    }

    @PostMapping("/request/{postId}")
    public ResponseEntity<ApiResponse<PlacementRequest>> requestPlacement(
            @PathVariable Long postId,
            Authentication authentication) {

        String userEmail = authentication.getName();
        PlacementRequest request = placementService.createRequest(postId, userEmail);
        return ResponseEntity.ok(new ApiResponse<PlacementRequest>(true, "Placement verification request sent to Admin", request));
    }

    @GetMapping("/admin/requests")
    public ResponseEntity<ApiResponse<List<PlacementRequest>>> getPendingRequests() {
        List<PlacementRequest> requests = placementService.getPendingRequests();
        return ResponseEntity.ok(new ApiResponse<List<PlacementRequest>>(true, "Pending requests retrieved", requests));
    }

    @PostMapping("/admin/approve/{requestId}")
    public ResponseEntity<ApiResponse<String>> approvePlacement(@PathVariable Long requestId) {
        String result = placementService.resolveRequest(requestId, true);
        return ResponseEntity.ok(new ApiResponse<String>(true, result, null));
    }

    @PostMapping("/admin/reject/{requestId}")
    public ResponseEntity<ApiResponse<String>> rejectPlacement(@PathVariable Long requestId) {
        String result = placementService.resolveRequest(requestId, false);
        return ResponseEntity.ok(new ApiResponse<String>(true, result, null));
    }
}